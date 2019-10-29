package org.smultron.framework.content.item.gathering;

import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.item.GatherItem;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

public class GatherBucketOfMilk extends TreeTask {
	public GatherBucketOfMilk(final TaskListener listener) {
		super(listener, "Going to milk a cow at lumbridge");
	}

	@Override
	public TreeNode onCreateRoot() {
		Task milkCow = new InteractWith<>("Milk", () -> SceneObjects.getNearest("Dairy cow"));
		Task getBucket = new GatherItem(null, "Bucket", false);
		TreeNode nearCow = new InArea(milkCow, CommonLocation.LUMBRIDGE_COWS, 2);
		TreeNode hasBucket = BinaryBranchBuilder.getNewInstance()
				.successNode(nearCow)
				.setValidation(() -> Inventory.contains("Bucket"))
				.failureNode(getBucket)
				.build();
		return hasBucket;
	}

	@Override
	public boolean validate() {
		return Inventory.contains("Bucket of milk");
	}
}
