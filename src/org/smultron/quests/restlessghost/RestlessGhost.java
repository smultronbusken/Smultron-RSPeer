package org.smultron.quests.restlessghost;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import org.smultron.framework.Location;
import org.smultron.framework.content.Equip;
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
		BooleanSupplier ghostLoadedOrClosedCoffin = () -> Npcs.getNearest("Restless ghost") != null;
		VarpBranch quest = new VarpBranch(Quest.THE_RESTLESS_GHOST.getVarpbit());

		String[] dialogAereck = new String[]{"I'm looking for a quest!", "Ok, let me help then."};
		TreeNode talkAereck = new ProcessDialogTree(() -> Npcs.getNearest("Father Aereck"), dialogAereck);
		TreeNode atAereck = new InArea(talkAereck, CommonLocation.LUMBRIDGE_CHURCH, 3);
		quest.put(0, atAereck);

		String[] dialogUrhney = new String[]{
				"Father Aereck sent me to talk to you.",
				"He's got a ghost haunting his graveyard."};
		TreeNode talkUrhney = new ProcessDialogTree(() -> Npcs.getNearest("Father Urhney"), dialogUrhney);
		TreeNode atUrhney = new InArea(talkUrhney, CommonLocation.LUMBRIDGE_FATHER_URHNEY, 1);
		quest.put(1, atUrhney);

		Supplier<Npc> restlessGhost = () -> Npcs.getNearest("Restless ghost");
		TreeNode talkToGhost = new ProcessDialogTree(restlessGhost, "Yep, now tell me what the problem is.");
		Task searchCoffin = new InteractWith<>("Search", () -> SceneObjects.getNearest("Coffin"));
		TreeNode shouldTalkToGhost = BinaryBranchBuilder.getNewInstance()
				.successNode(talkToGhost)
				.setValidation(ghostLoadedOrClosedCoffin)
				.failureNode(searchCoffin)
				.build();
		TreeNode atGhostGrave = new InArea(shouldTalkToGhost, CommonLocation.LUMBRIDGE_GHOST, 2);
		quest.put(2, hasGhostspeakAmulet(atGhostGrave));

		// Empty leaf node because the varp value will change as soon as we pick up the skull
		quest.put(3, hasSkull(new LeafNode()));

		BooleanSupplier isCoffinOpen = () -> {
			SceneObject coffin = SceneObjects.getNearest("Coffin");
			return coffin.containsAction("Close");
		};
		Task putSkull = new UseItemOn<>(() -> Inventory.getFirst("Ghost's skull"), () -> SceneObjects.getNearest("Coffin"));
		Task openCoffin = new InteractWith<>("Open", () -> SceneObjects.getNearest("Coffin"));
		TreeNode shouldPutSkull = BinaryBranchBuilder.getNewInstance()
				.successNode(putSkull)
				.setValidation(isCoffinOpen)
				.failureNode(openCoffin)
				.build();
		TreeNode atGhostGraveAfterSkull = new InArea(shouldPutSkull, CommonLocation.LUMBRIDGE_GHOST, 2);
		quest.put(4, hasSkull(hasGhostspeakAmulet(atGhostGraveAfterSkull)));

		return quest;
	}

	@Override
	public boolean validate() {
		return Varps.get(Quest.THE_RESTLESS_GHOST.getVarpbit()) == Quest.THE_RESTLESS_GHOST.getStages();
	}

	private TreeNode hasGhostspeakAmulet(TreeNode successNode) {
		Task equipAmulet = new Equip(null,  Equip.EquipType.WEAR, "Ghostspeak amulet");

		BooleanSupplier equipped = () -> ItemTables.contains(ItemTables.EQUIPMENT, s -> s.equals("Ghostspeak amulet"));

		TreeNode shouldEquip = BinaryBranchBuilder.getNewInstance()
				.successNode(successNode)
				.setValidation(equipped)
				.failureNode(equipAmulet)
				.build();

		TreeNode hasItems = BinaryBranchBuilder.getNewInstance()
				.successNode(shouldEquip)
				.setValidation(() -> Inventory.contains("Ghostspeak amulet") || equipped.getAsBoolean())
				.failureNode(getAmulet())
				.build();

		return hasItems;
	}

	private TreeNode getAmulet() {
		String option = "I've lost the Amulet of Ghostspeak.";
		TreeNode talk = new ProcessDialogTree(() -> Npcs.getNearest("Father Urhney"), option);
		TreeNode atFatherUrhney = new InArea(talk, CommonLocation.LUMBRIDGE_FATHER_URHNEY, 1);
		return atFatherUrhney;
	}

	private TreeNode hasSkull(TreeNode successNode) {
		Location altarRoom = Location.location(Area.rectangular(3111, 9564, 3121, 9568), " the altar room");
		Task takeSkull = new InteractWith<>("Search", () -> SceneObjects.getNearest("Altar"));
		TreeNode atAltar = new InArea(takeSkull, altarRoom, 3);
		TreeNode hasSkull = BinaryBranchBuilder.getNewInstance()
				.successNode(successNode)
				.setValidation(() -> Inventory.contains("Ghost's skull"))
				.failureNode(atAltar)
				.build();
		return hasSkull;
	}
}
