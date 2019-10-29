package org.smultron.framework.tasks;


/**
 * Used for knowing when a {@link Task}'s validate() method returns true.
 */
public interface TaskListener {
	public void onTaskComplete(Task task);
}
