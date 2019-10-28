package org.smultron.framework.thegreatforest;

import org.rspeer.ui.Log;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;

/**
 *
 */
public abstract class TreeNode implements Iterable<TreeNode>
{

    /**e
     * @return A {@link TreeTask} with this as the root node.
     */
    public Task asTask(final TaskListener listener, final String name) {
	TreeNode t = this;
	final Task task = new TreeTask(listener, name)
	{
	    @Override public TreeNode onCreateRoot() {
	    	return t;
	    }

	    @Override public boolean validate() {
	    	return false;
	    }
	};
 	return task;
    }

    protected TreeNode() {
    }

    public final boolean isLeaf() {
        return !this.iterator().hasNext();
    }
}
