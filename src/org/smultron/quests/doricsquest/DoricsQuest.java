package org.smultron.quests.doricsquest;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.Location;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.content.item.GatherItem;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.VarpBranch;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

public class DoricsQuest extends TreeTask
{
    public DoricsQuest(final TaskListener listener) {
	super(listener, "Completing Dorics Quest");
    }

    private static final Location DORICS_HOUSE = Location.location(Area.rectangular(2950, 3449, 2953, 3452), "Doric");

    @Override public TreeNode onCreateRoot() {
	VarpBranch quest = new VarpBranch(Quest.DORICS_QUEST.getVarpbit());

	String[] dialog = new String[]{
		"I wanted to use your anvils.",
		"Yes, I will get you the materials.",
		"Certainly, I'll be right back!"
	};
	TreeNode talkWithDoric = new ProcessDialogTree(dialog, () -> Npcs.getNearest("Doric"));
	TreeNode startQuest = new InArea(talkWithDoric, DORICS_HOUSE, 1);
	quest.put(0, hasItems(startQuest));

	TreeNode finishQuest = new InArea(new TalkToNpc(() -> Npcs.getNearest("Doric")), DORICS_HOUSE, 1);
	quest.put(10, hasItems(finishQuest));

	return quest;
    }

    @Override public boolean validate() {
	return Varps.get(Quest.DORICS_QUEST.getVarpbit()) == Quest.DORICS_QUEST.getStages();
    }

    private TreeNode hasItems(TreeNode successNode) {
	Task getItems = new ArrayTask("Gathering all items")
	{
	    @Override protected Task[] createTasks() {
		Task getClay = new GatherItem(this, "Clay", 6, true);
		Task getIronOre = new GatherItem(this, "Iron ore", 2, true);
		Task getCopperOre = new GatherItem(this, "Copper ore", 4, true);
		Task[] tasks = new Task[] { getClay, getIronOre, getCopperOre };
		return tasks;
	    }
	};
	BooleanSupplier inventoryContainsItem = () -> (Inventory.getCount("Clay") >= 6 &&
						       Inventory.getCount("Iron ore") >= 2 &&
						       Inventory.getCount("Copper ore") >= 4);
        TreeNode hasItems = BinaryBranchBuilder.getNewInstance()
		.successNode(successNode)
		.setValidation(inventoryContainsItem)
		.failureNode(getItems)
		.build();
        return hasItems;
    }

}
