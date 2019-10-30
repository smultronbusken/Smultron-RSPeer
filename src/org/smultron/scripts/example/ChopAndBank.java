package org.smultron.scripts.example;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSItemTable;
import org.smultron.framework.Location;
import org.smultron.framework.content.Equip;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.banking.GetItemFromBank;
import org.smultron.framework.content.banking.UnbankInventory;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.*;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ChopAndBank extends TreeTask {

	private final static Area TREE_AREA = Area.rectangular(3198, 3245, 3205, 3238);
	private final static Supplier<SceneObject> TREE_SUPPLIER = () -> SceneObjects.getNearest("Tree");

	public ChopAndBank() {
		super("Chopping trees in Lumbridge");
	}

	@Override
	public TreeNode onCreateRoot() {
		Task chopTree = new InteractWith<>("Chop down", TREE_SUPPLIER);

		// Walk to the area if we are not there.
		TreeNode atTreeArea = new InArea(chopTree, Location.location(TREE_AREA, "the tree area"), 5);

		Task dropInventory = new FunctionalTask(() -> {
			for(Item log : Inventory.getItems(item -> item.getName().equals("Logs"))){
				log.interact("Drop");
				Time.sleep(Random.nextInt(200, 900));
			}
		});

		TreeNode isInventoryFull = BinaryBranchBuilder.getNewInstance()
				.successNode(dropInventory) // If Inventory::isFull returns true
				.setValidation(Inventory::isFull)
				.failureNode(atTreeArea) // If Inventory::isFull returns false
				.build();
		return isInventoryFull;
	}

	@Override
	public boolean validate() {
		// The task will run forever
		return false;
	}
}
