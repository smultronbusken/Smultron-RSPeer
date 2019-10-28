package org.smultron.framework.thegreatforest;

import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.content.Idle;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link TreeNode} which wraps a {@link Task}.
 */
public class LeafNode extends TreeNode
{
    private Task task;

    /**
     * Wraps a {@link Idle} task.
     */
    public LeafNode() {
	this.task = new Idle();
    }

    /**
     */
    public LeafNode(final Task task) {
	this.task = task;
    }

    /**
     * Wraps a {@link Idle} task.
     * @param name the name of the task
     */
    public LeafNode(String name) {
        this.task = new Idle(name);
    }

    /**
     * @return the task of this {@link LeafNode}
     */
    @Override public Task asTask(final TaskListener listener, final String name) {
	return task;
    }

    @Override public Iterator<TreeNode> iterator() {
	Iterator<TreeNode> emptyIterator = new Iterator<TreeNode>()
	{
	    @Override public boolean hasNext() {
		return false;
	    }

	    @Override public TreeNode next() {
	        throw new NoSuchElementException("A LeafNode has no children!");
	    }
	};
        return emptyIterator;
    }

    public int executeTask() {
        return task.loop();
    };
}
