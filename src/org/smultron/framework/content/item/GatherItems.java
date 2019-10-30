package org.smultron.framework.content.item;

import org.rspeer.runetek.api.component.tab.Inventory;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.LeafNode;
import org.smultron.framework.thegreatforest.TreeNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


/**
 * Does not handle noted items
 * Assumes unlimited inventory space
 */
public class GatherItems extends TreeNode {
	private List<String> items;
	private Map<String, TreeNode> tasks;
	private boolean grandExchange;
	private Iterator<TreeNode> iterator;

	/**
	 *
	 */
	public GatherItems(List<String> items, TaskListener listener, boolean grandExchange) {
		this.items = items;
		this.grandExchange = grandExchange;
		tasks = new HashMap<>();

		// Set up all tasks
		for (String item : items) {
			Task gatherItem = new GatherItem(listener, item, grandExchange);
			tasks.put(item, new LeafNode(gatherItem));
		}
	}

	/**
	 * Finds the first item that isnt in our inventory.
	 * Return the task for that item
	 * If no missing item is found a empty {@link LeafNode} is returned.
	 */
	@Override
	public Iterator<TreeNode> iterator() {
		if (iterator != null)
			return iterator;

		iterator = new Iterator<TreeNode>() {
			@Override
			public boolean hasNext() {
				return !Inventory.containsAll((String[])items.toArray());
			}

			@Override
			public TreeNode next() {
				for (String item : items) {
					if (!Inventory.contains(item)) {
						return tasks.get(item);
					}
				}
				return new LeafNode();
			}
		};
		return iterator;
	}


	@Override
	public boolean isLeaf() {
		return false;
	}
}
