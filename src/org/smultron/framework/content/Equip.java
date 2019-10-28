package org.smultron.framework.content;

import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.smultron.framework.tasks.SimpleTask;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Equip items from the inventory.
 */
public class Equip extends ArrayTask
{
    @SuppressWarnings("JavaDoc")
    public enum EquipType {
        WEAR("Wear"), WIELD("Wield");

        public final String value;

	EquipType(final String value) {
	    this.value = value;
	}
    }

    private String[] equipment;
    private EquipType wearOrWield;

    /**
     * Assumes that all listed items exists in the inventory.
     * Otherwise it get stuck in an infinite loop while trying to equip.
     * @param equipment the items which will be equipped from inventory
     *                 All items must use the same interaction string for equipping
     * @param wearOrWield whether the items uses "Wear" or "Wield" as interaction
     */
    public Equip(final TaskListener listener, String[] equipment, EquipType wearOrWield) {
	super(listener, "Wielding " + Arrays.toString(equipment));
	this.equipment = equipment.clone();
	this.wearOrWield = wearOrWield;
    }

    /**
     * Closes the bank window
     * @return
     */
    @Override public int execute() {
	if (Bank.isOpen()) Bank.close();
	return super.execute();
    }

    @Override public boolean validate() {
	for (String itemName : equipment) {
	    if (isEquipped(itemName)) {
	        return false;
	    }
	}
	return true;
    }

    @Override protected Task[] createTasks() {
        // Filter out items which are already equipped
        String[] unequippedItems = Arrays.stream(equipment)
		.filter(s -> !isEquipped(s))
		.toArray(String[]::new);

        // Create a Task array and fill it with a task for each item
        Task[] tasks = new Task[unequippedItems.length];
	for (int i = 0; i < unequippedItems.length; i++) {
	    String itemName = unequippedItems[i];
	    Task equip = new InteractWith<>(wearOrWield.value, () -> Inventory.getFirst(itemName));
	    equip.attachListener(this);
	    tasks[i] = equip;
	}

	return tasks;
    }

    private boolean isEquipped(String itemName) {
	return ItemTables.contains(ItemTables.EQUIPMENT, item -> item.equals(itemName));
    }
}