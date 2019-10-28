package org.smultron.quests.doricsquest;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.TaskListener;
import org.smultron.framework.tasks.dialog.DoDialogTree;
import org.smultron.framework.tasks.dialog.TalkToNpcAndContinue;
import org.smultron.framework.tasks.movement.MoveTo;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.QuestBranch;
import org.smultron.framework.thegreatforest.ValidationBranch;
import org.smultron.info.FreeQuest;
import org.smultron.info.Location;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;

public class DoricsQuest extends TreeTask
{
    private int varpBit = FreeQuest.DORICS_QUEST.getVarpbit();
    private int questStages = FreeQuest.DORICS_QUEST.getStages();

    public DoricsQuest(final TaskListener listener) {
	super(listener, "Completing Dorics Quest");
    }

    @Override public TreeNode onCreateRoot() {
        /*
        Start quest
         */
        Deque<String> dialogStart = new ArrayDeque<>(Arrays.asList("I wanted to use your anvils.", "Yes, I will get you the materials.","Certainly, I'll be right back!"));
	dialogStart.addLast("I wanted to use your anvils.");
	dialogStart.addLast("Yes, I will get you the materials.");
	dialogStart.addLast("Certainly, I'll be right back!");
	TreeNode dialog = new DoDialogTree(dialogStart, "Doric");
	TreeNode startQuest = new ValidationBranch(dialog, new MoveTo(Location.DORICS_HOUSE, 2))
	{
	    @Override public boolean validate() {
		return Location.DORICS_HOUSE.getArea().contains(Players.getLocal());
	    }
	};

	/*
	Finish quest
	 */
	TreeNode finishQuest = new ValidationBranch(new TalkToNpcAndContinue("Doric"), new MoveTo(Location.DORICS_HOUSE, 2))
	{
	    @Override public boolean validate() {
		return Location.DORICS_HOUSE.getArea().contains(Players.getLocal());
	    }
	};

	/*
	Create the quest branch
	 */
	HashMap<Integer, TreeNode> questBranches = new HashMap<>();
	questBranches.put(0, new ConfirmWeHaveItems(startQuest));
	questBranches.put(10, new ConfirmWeHaveItems(finishQuest));
	TreeNode questTree = new QuestBranch(varpBit, questBranches);
	return questTree;
    }

    @Override public boolean validate() {
	return Varps.get(varpBit) == questStages;
    }
}
