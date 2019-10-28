package org.smultron.quests.doricsquest;

import org.rspeer.runetek.api.component.tab.Inventory;
import org.smultron.framework.SubScript;
import org.smultron.framework.Task;
import org.smultron.framework.tasks.item.GatherItem;
import org.smultron.framework.thegreatforest.LeafNode;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.SuccessBranch;


/**
 * Makes sure we have all items before continuing
 */
public class ConfirmWeHaveItems extends SuccessBranch
{
    // The task for getting all items.
    private Task getItems = new SubScript("Gathering all items")
    {
	@Override protected Task[] createTasks() {
	    Task getClay = new GatherItem(this, "Clay", 6, true);
	    Task getIronOre = new GatherItem(this, "Iron ore", 2, true);
	    Task getCopperOre = new GatherItem(this, "Copper ore", 4, true);
	    Task[] tasks = new Task[] {
	    getClay, getIronOre, getCopperOre
	    };
	    return tasks;
	}

	@Override public boolean validate() {
	    return (Inventory.getCount("Clay") >= 6 &&
	    Inventory.getCount("Iron ore") >= 2 &&
	    Inventory.getCount("Copper ore") >= 4);
	}
    };

    public ConfirmWeHaveItems(final TreeNode successNode) {
	super(successNode);
    }

    @Override public TreeNode failureNode() {
	return new LeafNode(getItems);
    }

    @Override public boolean validate() {
	return getItems.validate();
    }
}
