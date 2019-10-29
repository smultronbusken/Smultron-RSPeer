package org.smultron.framework.thegreatforest;

import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;


/**
 * Traverses a tree consisting of {@link TreeNode} until it finds a {@link LeafNode} which it executes.
 */
public abstract class TreeTask extends Task {

	private TreeNode root = null;
	private TreeNode currentNode = null;

	public TreeTask(final TaskListener listener, final String name) {
		super(listener, name);
	}

	public TreeTask(final String name) {
		super(name);
	}

	@Override
	public int execute() {
		if (root == null && currentNode == null) {
			root = onCreateRoot();
			currentNode = root;
			if (currentNode == null)
				throw new IllegalStateException("Root node is null.");
		}
		while (currentNode.iterator().hasNext()) {
			currentNode = currentNode.iterator().next();
		}
		int returnValue = ((LeafNode) currentNode).executeTask();
		currentNode = root;
		return returnValue;
	}

	/**
	 * @return The @see TreeTask the bot should execute first.
	 */
	public abstract TreeNode onCreateRoot();
}
