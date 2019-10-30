package org.smultron.quests.sheepshearer;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Npcs;
import org.smultron.framework.content.banking.GetItemFromBank;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.content.random.ShearSheep;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.*;

import java.util.function.Supplier;


/**
 * Assumes we have 21 free slots
 */
public class SheepShearer extends TreeTask {

	private static final Supplier<Npc> FRED_THE_FARMER = () -> Npcs.getNearest("Fred the Farmer");

	public SheepShearer(final TaskListener listener) {
		super(listener, "Completing Sheep Shearer.");
	}

	@Override
	public TreeNode onCreateRoot() {
		VarpBranch quest = new VarpBranch(Quest.SHEEP_SHEARER.getVarpbit());

		String[] dialogOption = new String[]{"I'm looking for a quest.", "Yes okay. I can do that."};
		TreeNode talkToFred = new ProcessDialogTree(FRED_THE_FARMER, dialogOption);
		TreeNode atFred = new InArea(talkToFred, CommonLocation.LUMBRIDGE_FREDTHEFARMER, 1);
		quest.put(0, atFred);

	/*
	 Shear the sheeps, then spin the balls
	 */
		Task getWool = new ArrayTask(null, "") {
			@Override
			protected Task[] createTasks() {
				//TODO this approach isnt bullet proof if we end up banking
				Task shearSheep = new ShearSheep(20, this);
				// We might not have all wools in out inventory after shearSheep is completed.
				Task getFromBank = new GetItemFromBank(this, "Wool", Bank.WithdrawMode.ITEM, 20);
				Task[] tasks = new Task[]{shearSheep, getFromBank};
				return tasks;
			}
		};
		Task spin = new SpinBallOfWool(null);
		TreeNode needMoreWool = BinaryBranchBuilder.getNewInstance()
				.successNode(spin)
				.setValidation(() -> Inventory.getCount("Wool") + Inventory.getCount("Ball of wool") == 20)
				.failureNode(getWool)
				.build();
		TreeNode giveFredTheBalls = new ProcessDialogTree(FRED_THE_FARMER, "I'm back!");
		TreeNode atFred2 = new InArea(giveFredTheBalls, CommonLocation.LUMBRIDGE_FREDTHEFARMER, 1);
		TreeNode hasBallOfWool = BinaryBranchBuilder.getNewInstance()
				.successNode(atFred2)
				.setValidation(() -> Inventory.getCount("Ball of wool") == 20)
				.failureNode(needMoreWool)
				.build();


		quest.put(1, hasBallOfWool);

		// We just need to click continue a couple of times
		TreeNode finish = new TalkToNpc(FRED_THE_FARMER);
		TreeNode atFred3 = new InArea(finish, CommonLocation.LUMBRIDGE_FREDTHEFARMER, 1);
		quest.put(20, atFred3);

		return quest;
	}

	@Override
	public boolean validate() {
		return Varps.get(Quest.SHEEP_SHEARER.getVarpbit()) == Quest.SHEEP_SHEARER.getStages();
	}
}
