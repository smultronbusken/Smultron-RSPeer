package org.smultron.quests.sheepshearer;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.SubScript;
import org.smultron.framework.Task;
import org.smultron.framework.TaskListener;
import org.smultron.framework.tasks.DoDialogTree;
import org.smultron.framework.tasks.banking.GetItemFromBank;
import org.smultron.framework.tasks.movement.MoveTo;
import org.smultron.framework.tasks.SpinBallOfWool;
import org.smultron.framework.tasks.dialog.TalkToNpcAndContinue;
import org.smultron.framework.tasks.item.GatherItem;
import org.smultron.framework.thegreatforest.LeafNode;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.BinaryBranch;
import org.smultron.framework.thegreatforest.QuestBranch;
import org.smultron.info.FreeQuest;
import org.smultron.info.Location;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

/**
 * TODO: Movement after shearing is fkced.
 */
public class SheepShearer extends TreeTask
{

    private final int varp = FreeQuest.SHEEP_SHEARER.getVarpbit();

    public SheepShearer(final TaskListener listener) {
	super(listener, "Completing Sheep Shearer.");
    }

    @Override public TreeNode onCreateRoot() {
	HashMap<Integer, TreeNode> varpTable = new HashMap<>();
	TreeNode start = new LeafNode(new StartQuest(null));
	varpTable.put(0, start);

	/*
	 Shear the sheeps, then spin the balls
	 */
	Task getWool = new SubScript(null, "")
	{
	    @Override protected Task[] createTasks() {
		return new Task[] {
		    new GatherItem(this, "Wool", 20, false),
		    new GetItemFromBank(this, "Wool", Bank.WithdrawMode.ITEM, true)
		};
	    }
	};

	Task spin = new SpinBallOfWool(null);
	TreeNode spinWool = new BinaryBranch()
	{
	    @Override public TreeNode failureNode() {
		return new LeafNode(getWool);
	    }

	    @Override public TreeNode successNode() {
		return new LeafNode(spin);
	    }

	    @Override public boolean validate() {
		return Inventory.getCount("Wool") + Inventory.getCount("Ball of wool") == 20;
	    }
	};

	/*
	Give the balls to Fred
	 */
	Task walkToFred = new MoveTo(Location.LUMBRIDGE_FREDTHEFARMER, 2);
	Deque<String> dialogOptions = new ArrayDeque<>();
	dialogOptions.addLast("I'm back!");
	TreeNode talkToFred = new DoDialogTree(dialogOptions, "Fred the Farmer");
	TreeNode atFred = new BinaryBranch()
	{
	    @Override public TreeNode failureNode() {
		return new LeafNode(walkToFred);
	    }

	    @Override public TreeNode successNode() {
		return talkToFred;
	    }

	    @Override public boolean validate() {
		return Location.LUMBRIDGE_FREDTHEFARMER.getArea().contains(Players.getLocal());
	    }
	};

	TreeNode giveFredBalls = new BinaryBranch()
	{
	    @Override public TreeNode failureNode() {
		return spinWool;
	    }

	    @Override public TreeNode successNode() {
		return atFred;
	    }

	    @Override public boolean validate() {
		return Inventory.getCount("Ball of wool") == 20;
	    }
	};
	varpTable.put(1, giveFredBalls);

	// We just need to click continue a couple of times
	TreeNode finish = new TalkToNpcAndContinue("Fred the Farmer", null);
	varpTable.put(20, finish);
	return new QuestBranch(varp, varpTable);
    }

    @Override public boolean validate() {
	return Varps.get(varp) == FreeQuest.SHEEP_SHEARER.getStages();
    }
}
