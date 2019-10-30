package org.smultron.quests.xmarksthespot;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.Location;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.content.item.GatherItem;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.*;

import java.util.function.Supplier;

/**
 * Assumes 3 free inventory slots
 */
public class XMarksTheSpot extends TreeTask {

	private static final Supplier<Npc> VEOS = () -> Npcs.getNearest("Veos");

	public XMarksTheSpot(final TaskListener listener) {
		super(listener, "Completing X Marks the Spot");
	}

	@Override
	public TreeNode onCreateRoot() {
		VarpBranch quest = new VarpBranch(Quest.X_MARKS_THE_SPOT.getVarpbit());

	/*
	Start quest
	 */
		String[] startDialog = new String[]{
				"I'm looking for a quest.",
				"Sounds good, what should I do?"
		};
		TreeNode talkToVeos = new ProcessDialogTree(VEOS, startDialog);
		TreeNode atVeos = new InArea(talkToVeos, CommonLocation.VEOS, 2);
		quest.put(0, atVeos);
		quest.put(1, new InArea(new TalkToNpc(VEOS), CommonLocation.VEOS, 2)); // Need to continue the dialogue a couple of times.

	/*
	Get all the clues
	 */
		Position firstCluePosition = new Position(3230, 3209);
		TreeNode shouldDigFirst = walkToAndDig(firstCluePosition);
		quest.put(2, shouldDigFirst);

		Position secondCluePosition = new Position(3203, 3212);
		TreeNode shouldDigSecond = walkToAndDig(secondCluePosition);
		quest.put(3, shouldDigSecond);

		Position thirdCluePosition = new Position(3108, 3264);
		TreeNode shouldDigThird = walkToAndDig(thirdCluePosition);
		quest.put(4, shouldDigThird);

		Position fourthCluePosition = new Position(3078, 3260);
		TreeNode shouldDigFourth = walkToAndDig(fourthCluePosition);
		quest.put(5, shouldDigFourth);

	/*
	Talk to Veos
	 */
		Area veosBoat = Area.rectangular(3050, 3245, 3055, 3249);
		TreeNode atVeosBoat = new InArea(new TalkToNpc(VEOS), Location.location(veosBoat, " Veos at the dock."), 2);
		quest.put(6, atVeosBoat);
		quest.put(7, atVeosBoat);

	/*
	Always make sure we have a spade
	 */
		Task getSpade = new GatherItem(null, "Spade", false);
		TreeNode hasSpade = BinaryBranchBuilder.getNewInstance()
				.successNode(quest)
				.setValidation(() -> Inventory.contains("Spade"))
				.failureNode(getSpade)
				.build();
		return hasSpade;
	}

	@Override
	public boolean validate() {
		// For some reason the varpbit doesnt stay on a value after completing the quest
		// But it does seem to always stay over 7
		return Varps.get(Quest.X_MARKS_THE_SPOT.getVarpbit()) > 7;
	}

	private TreeNode walkToAndDig(Position cluePosition) {
		Task dig = new InteractWith<>("Dig", () -> Inventory.getFirst("Spade"));
		Task moveToPosition = new MoveTo(Location.location(cluePosition), 0);
		TreeNode isAtCorrectPosition = BinaryBranchBuilder.getNewInstance()
				.successNode(dig)
				.setValidation(() -> Players.getLocal().getPosition().equals(cluePosition))
				.failureNode(moveToPosition)
				.build();
		return isAtCorrectPosition;
	}
}
