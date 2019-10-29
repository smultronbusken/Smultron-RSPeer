package org.smultron.framework.thegreatforest;

import org.rspeer.runetek.api.Varps;
import org.rspeer.ui.Log;
import org.smultron.framework.tasks.Task;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Returns different {@link TreeNode} depending on the current value of a varpbit
 */
public class VarpBranch extends TreeNode {
	private int varpbit;
	private HashMap<Integer, TreeNode> varpTable;

	/**
	 *
	 */
	public VarpBranch(final int varpbit) {
		varpTable = new HashMap<>();
		this.varpbit = varpbit;
	}

	@Override
	public Iterator<TreeNode> iterator() {
		Iterator<TreeNode> iterator = new Iterator<TreeNode>() {
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public TreeNode next() {
				int varpValue = Varps.get(varpbit);
				TreeNode nextTask = varpTable.get(varpValue);
				if (nextTask != null) {
					return nextTask;
				} else {
					Log.severe("Couldnt match the varpbit " + varpbit + " with the value " + varpValue + " to a task.");
					return new LeafNode();
				}
			}
		};
		put(12, new LeafNode());
		return iterator;
	}

	/**
	 * Match the a varp value with a given {@link TreeNode}
	 *
	 * @param varpValue the varp value
	 * @param treeNode  the node which will get returned
	 */
	public void put(int varpValue, TreeNode treeNode) {
		varpTable.put(varpValue, treeNode);
	}

	/**
	 * Match the a varp value with a given {@link Task}
	 *
	 * @param varpValue the varp value
	 * @param task      the task
	 */
	public void put(int varpValue, Task task) {
		varpTable.put(varpValue, new LeafNode(task));
	}
}
