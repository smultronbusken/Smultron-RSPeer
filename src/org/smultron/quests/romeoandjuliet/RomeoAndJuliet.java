package org.smultron.quests.romeoandjuliet;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.content.item.GatherItem;
import org.smultron.framework.info.Quest;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.VarpBranch;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Assumes we have 2 free inventory slots
 */
public class RomeoAndJuliet extends TreeTask
{

    private int varpBit = Quest.ROMEO_AND_JULIET.getVarpbit();
    private int questStages = Quest.ROMEO_AND_JULIET.getStages();

    private static final Supplier<Npc> JULIET = () -> Npcs.getNearest("Juliet");
    private static final Supplier<Npc> ROMEO = () -> Npcs.getNearest("Romeo");
    private static final Area JULIET_PORCH = Area.rectangular(3153, 3423, 3167, 3436, 1);

    public RomeoAndJuliet(final TaskListener listener) {
	super(listener, "Completing Romeo and Juliet");
    }

    private TreeNode makeSureWeHaveCadava(TreeNode nextNode) {
        Task gatherCadava = new GatherItem(null, "Cadava berries", 1, true);

        TreeNode shouldGatherCadava = BinaryBranchBuilder.getNewInstance()
		.successNode(nextNode)
		.setValidation(() -> Inventory.contains("Cadava berries"))
		.failureNode(gatherCadava)
		.build();

        return shouldGatherCadava;
    }

    private TreeNode makeSureWeHaveMessage(TreeNode nextNode) {
        TreeNode talkToJuliet = new TalkToNpc(JULIET);
        TreeNode getNewLetter = new InArea(talkToJuliet, CommonLocation.VARROCK_JULIET, 5);

        TreeNode shouldGetNewLetter = BinaryBranchBuilder.getNewInstance()
		.successNode(nextNode)
		.setValidation(() -> Inventory.contains("Message"))
		.failureNode(getNewLetter)
		.build();

        return shouldGetNewLetter;
    }


    @Override public TreeNode onCreateRoot() {
	VarpBranch quest = new VarpBranch(Quest.ROMEO_AND_JULIET.getVarpbit());
        Task walkToJuliet = new MoveTo(CommonLocation.VARROCK_JULIET, 0);

        /*
        Start the quest by getting Cadava berries and talking to Juliet
         */
	String[] startQuestDialog = new String[]{
		"Yes I've met him.",
		"Certainly, I'll do so straight away."
	};
        TreeNode talkToJuliet = new ProcessDialogTree(startQuestDialog, JULIET);
        TreeNode startQuest = BinaryBranchBuilder.getNewInstance()
		.successNode(talkToJuliet)
		.setValidation(() -> CommonLocation.VARROCK_JULIET.asArea().contains(Players.getLocal()) || JULIET_PORCH.contains(Players.getLocal()))
		.failureNode(walkToJuliet)
		.build();
        quest.put(0, makeSureWeHaveCadava(startQuest));

        /*
        Talk to Romeo
         */
        TreeNode talkToRomeo = new TalkToNpc(ROMEO);
        TreeNode atTownSquare = new InArea(talkToRomeo, CommonLocation.VARROCK_ROMEO, 5);
        quest.put(20, makeSureWeHaveMessage(makeSureWeHaveCadava(atTownSquare)));

        /*
        Talk to father
         */
	TreeNode talkToLawrence = new TalkToNpc(() -> Npcs.getNearest("Father Lawrence"));
	TreeNode nearLawrence = new InArea(talkToLawrence, CommonLocation.VARROCK_CHURCH, 4);
	quest.put(30, makeSureWeHaveCadava(nearLawrence));

	/*
	Talk to the Apothecary
	 */
	String[] getPotionDialog = new String[]{
		"Talk about something else.",
		"Talk about Romeo & Juliet."
	};
	TreeNode talkToApothecary = new ProcessDialogTree(getPotionDialog, () -> Npcs.getNearest("Apothecary"));
	TreeNode atApothecary = new InArea(talkToApothecary, CommonLocation.VARROCK_APOTHECARY, 2);
	quest.put(40, makeSureWeHaveCadava(atApothecary));

	/*
	Finish talk with Apothecary and give potion to Juliet
	 */
	TreeNode finishTalkWithApothecary = new InArea(new TalkToNpc(() -> Npcs.getNearest("Apothecary")), CommonLocation.VARROCK_APOTHECARY, 2);
	TreeNode giveJulietThePotion = startQuest; // Giving the potion to Juliet is the exact same as starting the quest.
	// Temporary fix
	BooleanSupplier temporaryFix = () -> {
	    if(Dialog.canContinue() && Npcs.getLoaded(npc -> npc.getName().equals("Apothecary")) != null) {
		Dialog.processContinue();
		Time.sleepUntil(() -> !Dialog.isProcessing(), 2000);
		if(Dialog.canContinue() && Npcs.getLoaded(npc -> npc.getName().equals("Apothecary")) != null) {
		    Dialog.processContinue();
		    Time.sleepUntil(() -> !Dialog.isProcessing(), 2000);
		}
	    }
	    return Inventory.contains("Cadava potion");
	};
	TreeNode hasPotion = BinaryBranchBuilder.getNewInstance()
		.successNode(giveJulietThePotion)
		.setValidation(temporaryFix)
		.failureNode(finishTalkWithApothecary)
		.build();
	quest.put(50, hasPotion);

	/*
	Finish up
	*/
	TreeNode finishQuest = new InArea(new TalkToNpc(ROMEO), CommonLocation.VARROCK_ROMEO, 8);
	quest.put(60, finishQuest);

	return quest;
    }

    @Override public boolean validate() {
	return Varps.get(varpBit) == questStages;
    }
}
