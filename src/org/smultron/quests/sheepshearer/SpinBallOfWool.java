package org.smultron.quests.sheepshearer;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Production;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.content.UseItemOn;
import org.smultron.framework.content.banking.GetItemFromBank;
import org.smultron.framework.content.production.ProduceItem;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.info.Region;

public class SpinBallOfWool extends TreeTask
{
    public SpinBallOfWool(final TaskListener listener) {
	super(listener, "Spinning balls of wool");
    }

    @Override public TreeNode onCreateRoot() {
	Task openSpinWheel = new UseItemOn<SceneObject>(null, () -> Inventory.getFirst("Wool"), () -> SceneObjects.getNearest("Spinning wheel"));
	Task produce = new ProduceItem(null, "Ball of wool");
        Task fillInventory = new GetItemFromBank(null, "Wool", Bank.WithdrawMode.ITEM, 28);

        TreeNode isProductionScreenOpen = BinaryBranchBuilder.getInstance()
		.successNode(produce)
		.setValidation(Production::isOpen)
		.failureNode(openSpinWheel)
		.build();
        TreeNode atSpinningWheel = new InArea(isProductionScreenOpen, Region.LUMBRIDGE_SPINNINGWHEEL, 1);
        TreeNode hasWool = BinaryBranchBuilder.getInstance()
		.successNode(atSpinningWheel)
		.setValidation(() -> Inventory.contains("Wool"))
		.failureNode(fillInventory)
		.build();

	return hasWool;
    }

    @Override public boolean validate() {
	return false;
    }
}
