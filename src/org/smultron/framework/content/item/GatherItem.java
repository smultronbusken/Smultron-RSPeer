package org.smultron.framework.content.item;

import org.rspeer.runetek.api.component.Bank.WithdrawMode;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.smultron.framework.content.banking.GetItemFromBank;
import org.smultron.framework.content.grandexchange.BuyCollectUnnote;
import org.smultron.framework.content.item.gathering.GatheringTasks;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;

/**
 * Will try the following tasks in order until the item is obtained:
 * 1. Check the bank
 * 2/3. Buy it from GE, if set to.
 * 2/3. (TODO) Execute a backup task, if provided with such task.
 * 4. Try to get a task from {@link GatheringTasks} and execute it
 * If all of these fails, it will get stuck.
 * <p>
 * Does not handle noted items
 * Assumes unlimited inventory space
 */
public class GatherItem extends ArrayTask {
	private String item;
	private int amount;
	private boolean buyAtGrandExchange;
	private Task backUpTask;

	/**
	 * @param buyAtGrandExchange if true, we try to buy the item(s) from GE
	 */
	public GatherItem(final TaskListener listener, final String item, boolean buyAtGrandExchange) {
		super(listener, "Gathering " + item);
		this.item = item;
		this.amount = 1;
		this.buyAtGrandExchange = buyAtGrandExchange;

	}

	/**
	 * @param buyAtGrandExchange if true, we try to buy the item(s) from GE
	 */
	public GatherItem(final TaskListener listener, final String item, final int amount, boolean buyAtGrandExchange) {
		super(listener, "Gathering " + item);
		this.item = item;
		this.amount = amount;
		this.buyAtGrandExchange = buyAtGrandExchange;
	}

	@Override
	public boolean validate() {
		boolean isEquipped = ItemTables.contains(ItemTables.EQUIPMENT, equipmentName -> equipmentName.equals(item));
		boolean correctAmount = Inventory.getCount(true, item) >= amount;
		return isEquipped || correctAmount;
	}

	@Override
	protected Task[] createTasks() {
		Task fromBank = new GetItemFromBank(this, item, WithdrawMode.ITEM, amount);
		Task gather = GatheringTasks.getTaskFor(this, item, amount);

		Task[] tasks;
		if (buyAtGrandExchange) {
			Task buyFromGrandExchange = new BuyCollectUnnote(this, item, amount);
			tasks = new Task[]{fromBank, buyFromGrandExchange, fromBank, gather};
		} else {
			tasks = new Task[]{fromBank, gather};
		}

		return tasks;
	}

}
