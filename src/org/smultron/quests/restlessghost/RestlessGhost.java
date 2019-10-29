package org.smultron.quests.restlessghost;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import org.smultron.framework.Location;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.UseItemOn;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.*;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * TODO: Make sure we are wearing the amulet does not work if the user pauses midquest and drops it
 * TODO; If the user pauses at the ghost parts until the ghost dissapears, it will get stuck.
 */
public class RestlessGhost extends TreeTask {
	public RestlessGhost(TaskListener listener) {
		super(listener, "Completing The Restless Ghost");
	}

	@Override
	public TreeNode onCreateRoot() {
		BooleanSupplier isGhostLoaded = () -> Npcs.getLoaded(npc -> npc.getName().equals("Restless ghost")) != null;
		VarpBranch quest = new VarpBranch(Quest.THE_RESTLESS_GHOST.getVarpbit());

		String[] dialogAereck = new String[]{"I'm looking for a quest!", "Ok, let me help then."};
		TreeNode talkAereck = new ProcessDialogTree(dialogAereck, () -> Npcs.getNearest("Father Aereck"));
		TreeNode atAereck = new InArea(talkAereck, CommonLocation.LUMBRIDGE_CHURCH, 3);
		quest.put(0, atAereck);

		String[] dialogUrhney = new String[]{
				"Father Aereck sent me to talk to you.",
				"He's got a ghost haunting his graveyard."};
		TreeNode talkUrhney = new ProcessDialogTree(dialogUrhney, () -> Npcs.getNearest("Father Urhney"));
		TreeNode atUrhney = new InArea(talkUrhney, CommonLocation.LUMBRIDGE_FATHER_URHNEY, 1);
		quest.put(1, atUrhney);

		Supplier<Npc> restlessGhost = () -> Npcs.getNearest("Restless ghost");
		TreeNode talkToGhost = new ProcessDialogTree("Yep, now tell me what the problem is.", restlessGhost);
		Task openCoffin = new InteractWith<>("Search", () -> SceneObjects.getNearest("Coffin"));
		TreeNode isCoffinOpen = BinaryBranchBuilder.getNewInstance()
				.successNode(talkToGhost)
				.setValidation(isGhostLoaded)
				.failureNode(openCoffin)
				.build();
		TreeNode atGhostGrave = new InArea(isCoffinOpen, CommonLocation.LUMBRIDGE_GHOST, 2);
		quest.put(2, hasGhostspeakAmulet(atGhostGrave));

		// Empty leaf node because the varp value will change as soon as we pick up the skull
		quest.put(3, hasSkull(new LeafNode()));

		Task putSkull = new UseItemOn<>(() -> Inventory.getFirst("Ghost's skull"), () -> SceneObjects.getNearest("Coffin"));
		TreeNode atGhostGraveAfterSkull = new InArea(putSkull, CommonLocation.LUMBRIDGE_GHOST, 2);
		quest.put(4, hasSkull(hasGhostspeakAmulet(atGhostGraveAfterSkull)));

		return quest;
	}

	@Override
	public boolean validate() {
		return Varps.get(Quest.THE_RESTLESS_GHOST.getVarpbit()) == Quest.THE_RESTLESS_GHOST.getStages();
	}

	private TreeNode hasGhostspeakAmulet(TreeNode successNode) {
		BooleanSupplier inInventoryOrEquipped = () -> {
			boolean inInventory = Inventory.contains(4250, 552);
			boolean isEquipped = ItemTables.lookup(ItemTables.EQUIPMENT).contains(4250, 522);
			return isEquipped || inInventory;
		};
		TreeNode hasItems = BinaryBranchBuilder.getNewInstance()
				.successNode(successNode)
				.setValidation(inInventoryOrEquipped)
				.failureNode(new FunctionalTask(() -> Log.severe("I have note amulet"))) // TODO
				.build();
		return hasItems;
	}

	private TreeNode hasSkull(TreeNode successNode) {
		Location altarRoom = Location.location(Area.rectangular(3111, 9564, 3121, 9568), " the altar room");
		Task takeSkull = new InteractWith<>("Search", () -> SceneObjects.getNearest("Altar"));
		TreeNode atAltar = new InArea(takeSkull, altarRoom, 3);
		TreeNode hasSkull = BinaryBranchBuilder.getNewInstance()
				.successNode(successNode)
				.setValidation(() -> Inventory.contains("Skull"))
				.failureNode(atAltar)
				.build();
		return hasSkull;
	}
}
