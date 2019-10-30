package org.smultron.framework.content.random;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Npcs;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.banking.BankCache;
import org.smultron.framework.content.banking.UnbankInventory;
import org.smultron.framework.content.item.GatherItem;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Shear sheeps at Lumbridge
 */
public class ShearSheep extends TreeTask implements TaskListener {
	private int amount;
	private int currentAmount;

	public ShearSheep(final int amount, final TaskListener listener) {
		super(listener, "Gathering " + amount + " wools");
		this.amount = amount;
		currentAmount = Inventory.getCount("Wool");
	}

	@Override
	public void reset() {
		currentAmount = Inventory.getCount("Wool");
		super.reset();
	}

	@Override
	public TreeNode onCreateRoot() {
		Task gatherShear = new GatherItem(null, "Shears", false);
		Predicate<? super Npc> sheep = npc -> (npc.getName().equals("Sheep") &&
				!Arrays.asList(npc.getActions()).contains("Talk-to") &&
				Arrays.asList(npc.getActions()).contains("Shear") &&
				CommonLocation.LUMBRIDGE_SHEEPS.asArea().contains(npc));
		Task shear = new InteractWith<>("Shear", Npcs::getNearest, sheep);
		shear.attachListener(this);
		Task unbank = new UnbankInventory(this);

		TreeNode atSheeps = new InArea(shear, CommonLocation.LUMBRIDGE_SHEEPS, 7);
		TreeNode hasShear = BinaryBranchBuilder.getNewInstance()
				.successNode(atSheeps)
				.setValidation(() -> Inventory.contains("Shears"))
				.failureNode(gatherShear)
				.build();
		TreeNode isInventoryFull = BinaryBranchBuilder.getNewInstance()
				.successNode(unbank)
				.setValidation(Inventory::isFull)
				.failureNode(hasShear)
				.build();

		return isInventoryFull;
	}

	@Override
	public boolean validate() {
		status = ", Wools collected: " + currentAmount + ". In bank: "
				+ (BankCache.getInstance().mustCheckBank() ? "?" : BankCache.getInstance().getCount(true, "Wool"));
		return total() >= amount;
	}

	@Override
	public void onTaskComplete(Task task) {
		task.reset();
		currentAmount = Inventory.getCount("Wool");
		try {
			UnbankInventory iShallNotBeUsed = (UnbankInventory) task;
			currentAmount = 0;
		} catch (ClassCastException ignore) {
		}
	}

	private int total() {
		if (!BankCache.getInstance().mustCheckBank())
			return currentAmount + BankCache.getInstance().getCount(true, "Wool");
		else
			return currentAmount;
	}
}
