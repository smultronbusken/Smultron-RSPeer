package org.smultron.quests.impcatcher;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.Location;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;

public class ImpCatcher extends TreeTask
{
    private int varpBit = Quest.IMP_CATCHER.getVarpbit();
    private int questStages = Quest.IMP_CATCHER.getStages();

    private static final Location WIZARD_MIZGOG = Location.location(Area.polygonal(new Position(3107, 3162, 2),
										   new Position(3107, 3156, 2),
										   new Position(3103, 3159, 2),
										   new Position(3102, 3164, 2),
										   new Position(3103, 3167, 2),
										   new Position(3105, 3167, 2)), "Wizard Mizgog");

    public ImpCatcher(final TaskListener listener) {
	super(listener, "Completing Imp Catcher");
    }

    @Override public TreeNode onCreateRoot() {
        /*
        Speak with Wizard Mizgog
         */
	TreeNode dialog = new ProcessDialogTree("Give me a quest please.", () -> Npcs.getNearest("Wizard Mizgsog"));


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
