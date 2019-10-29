package org.smultron.scripts.example;

import org.rspeer.runetek.adapter.scene.SceneObject;
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
		Task unbank = new UnbankInventory(null);

		Task chopTree = new InteractWith<>("Chop down", TREE_SUPPLIER);
		TreeNode atTreeArea = new InArea(chopTree, Location.location(TREE_AREA, "the tree area"), 5);

		TreeNode isInventoryFull = BinaryBranchBuilder.getNewInstance()
				.successNode(atTreeArea) // If Inventory::isFull returns true
				.setValidation(Inventory::isFull)
				.failureNode(chopTree) // If Inventory::isFull returns false
				.build();

		return axeEquipped(isInventoryFull);
	}

	@Override
	public boolean validate() {
		// The task will run forever
		return false;
	}

	private TreeNode axeEquipped(TreeNode successNode) {
		BooleanSupplier isAxeEquipped = () -> {
			RSItemTable equipment = ItemTables.lookup(ItemTables.EQUIPMENT);
			return equipment != null && equipment.contains(1351);
		};
		Task getAxe = new GetItemFromBank(null, "Bronze axe", Bank.WithdrawMode.ITEM, 1);
		Task equipAxe = new InteractWith<>("Wield", () -> Inventory.getFirst("Bronze axe"));

		TreeNode shouldGetAxe = BinaryBranchBuilder.getNewInstance()
				.successNode(equipAxe)
				.setValidation(() -> Inventory.contains("Bronze axe"))
				.failureNode(getAxe)
				.build();

		TreeNode shouldEquipAxe = BinaryBranchBuilder.getNewInstance()
				.successNode(successNode)
				.setValidation(isAxeEquipped)
				.failureNode(shouldGetAxe)
				.build();

		return shouldEquipAxe;
	}
}
