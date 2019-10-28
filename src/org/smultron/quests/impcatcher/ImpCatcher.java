package org.smultron.quests.impcatcher;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.Task;
import org.smultron.framework.TaskListener;
import org.smultron.framework.tasks.dialog.DoDialogTree;
import org.smultron.framework.tasks.item.GatherItems;
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

public class ImpCatcher extends TreeTask
{
    private int varpBit = FreeQuest.IMP_CATCHER.getVarpbit();
    private int questStages = FreeQuest.IMP_CATCHER.getStages();

    public ImpCatcher(final TaskListener listener) {
	super(listener, "Completing Imp Catcher");
    }

    @Override public TreeNode onCreateRoot() {
        /*
        Speak with Wizard Mizgog
         */
	Deque<String> dialogStart = new ArrayDeque<>();
	dialogStart.addLast("Give me a quest please.");
	TreeNode dialog = new DoDialogTree(dialogStart, "Wizard Mizgog");
	TreeNode startQuest = new ValidationBranch(dialog, new MoveTo(Location.WIZARD_MIZGOG, 1))
	{
	    @Override public boolean validate() {
		return Location.WIZARD_MIZGOG.getArea().contains(Players.getLocal());
	    }
	};

	/*
	Get all beads
	 */
	Task getItems = new GatherItems(Arrays.asList("Black bead", "Yellow bead", "Red bead", "White bead"), null, true);
	TreeNode confirmWeHaveItems = new ValidationBranch(startQuest, getItems)
	{
	    @Override public boolean validate() {
		return getItems.validate();
	    }
	};

        /*
	Create the quest branch
	 */
	HashMap<Integer, TreeNode> questBranches = new HashMap<>();
	questBranches.put(0, confirmWeHaveItems);
	TreeNode questTree = new QuestBranch(varpBit, questBranches);
	return questTree;
    }

    @Override public boolean validate() {
	return Varps.get(varpBit) == questStages;
    }
}
