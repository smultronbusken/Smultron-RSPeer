package org.smultron.framework.content.item.gathering;

import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.smultron.framework.Location;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.content.item.GatherItem;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

import java.util.function.BooleanSupplier;


public class GatherPotOfFlour extends TreeTask {
	private static final Location LUMBRIDGE_MILL = Location.location(Area.rectangular(3163, 3309, 3169, 3304, 0), " Lumbridge mill");
	private static final Location LUMBRIDGE_MILL_SECOND_FLOOR = Location.location(Area.rectangular(3163, 3309, 3169, 3304, 2), " the top floor");

	public GatherPotOfFlour(final TaskListener listener) {
		super(listener, "Gathering some flour at the Lumbridge mill.");
	}

	@Override
	public TreeNode onCreateRoot() {
		Task gatherPot = new GatherItem(null, "Pot", false);
		Task gatherWheat = new GatherItem(null, "Grain", false);
		Task operateLumbridgeMill = operateLumbridgeMill();

		BooleanSupplier didUseGrainValidation = () -> {
			return (Inventory.contains("Grain")) || LUMBRIDGE_MILL_SECOND_FLOOR.asArea().setIgnoreFloorLevel(true).contains(Players.getLocal());
		};
		TreeNode hasPutGrainInHopper = BinaryBranchBuilder.getNewInstance()
				.successNode(operateLumbridgeMill)
				.setValidation(didUseGrainValidation)
				.failureNode(gatherWheat)
				.build();

		TreeNode hasPot = BinaryBranchBuilder.getNewInstance()
				.successNode(hasPutGrainInHopper)
				.setValidation(() -> Inventory.contains("Pot"))
				.failureNode(gatherPot)
				.build();

		return hasPot;
	}

	@Override
	public boolean validate() {
		return Inventory.contains("Pot of flour");
	}

	private Task operateLumbridgeMill() {
		return new ArrayTask(null, "Operating mill.") {
			@Override
			protected Task[] createTasks() {
				Task moveToHopper = new MoveTo(this, LUMBRIDGE_MILL_SECOND_FLOOR, 1);
				Task fillHopper = new InteractWith<>("Fill", () -> SceneObjects.getNearest("Hopper"));
				Task operateControls = new InteractWith<>("Operate", () -> SceneObjects.getNearest("Hopper controls"));
				Task moveToBin = new MoveTo(this, LUMBRIDGE_MILL, 1);
				Task retrieveFlour = new InteractWith<>("Empty", () -> SceneObjects.getNearest("Flour bin"));

				operateControls.attachListener(this);
				retrieveFlour.attachListener(this);
				fillHopper.attachListener(this);

				Task[] tasks = new Task[]{
						moveToHopper,
						fillHopper,
						operateControls,
						moveToBin,
						retrieveFlour
				};

				return tasks;
			}

			@Override
			public void onTaskComplete(final Task task) {
				super.onTaskComplete(task);
				task.reset();
			}
		};
	}
}
