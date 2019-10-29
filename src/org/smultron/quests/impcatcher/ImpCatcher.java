package org.smultron.quests.impcatcher;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.smultron.framework.Location;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.content.item.GatherItems;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.VarpBranch;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.function.Supplier;

public class ImpCatcher extends TreeTask
{
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
        VarpBranch quest = new VarpBranch(Quest.IMP_CATCHER.getVarpbit());
	Supplier<Npc> mizgog = () -> Npcs.getNearest("Wizard Mizgog");

	TreeNode startDialog = new ProcessDialogTree("Give me a quest please.", mizgog);
	TreeNode atMizgogStart = new InArea(startDialog, WIZARD_MIZGOG, 1);
	quest.put(0, hasItems(atMizgogStart));

	TreeNode endDialog = new TalkToNpc(mizgog);
	TreeNode atMizgogEnd = new InArea(endDialog, WIZARD_MIZGOG, 1);
	quest.put(1, hasItems(atMizgogEnd));

	return quest;
    }

    @Override public boolean validate() {
	return Varps.get(Quest.IMP_CATCHER.getVarpbit()) == Quest.IMP_CATCHER.getStages();
    }

    private TreeNode hasItems(TreeNode successNode) {
        String[] items = new String[] { "Black bead", "Yellow bead", "Red bead", "White bead" };
	TreeNode getItems = new GatherItems(Arrays.asList(items), null, true);
	TreeNode hasItems = BinaryBranchBuilder.getNewInstance()
		.successNode(successNode)
		.setValidation(() -> Inventory.contains(items))
		.failureNode(getItems)
		.build();
	return hasItems;
    }
}
