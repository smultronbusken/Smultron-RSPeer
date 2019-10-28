package org.smultron.framework.tasks;

import org.rspeer.ui.Log;

/**
 * A task that executes a list of {@link Task}'s in order. When all tasks are completed, start the first task again.
 * When a task is completed, it will not do a validation check for that task until all tasks has been completed.
 */
public abstract class ArrayTask extends Task implements TaskListener
{
    private Task[] tasks = null;
    protected int taskIndex = 0;

    /**
     *
     * @param listener
     * @param name
     */
    protected ArrayTask(final TaskListener listener, String name) {
	super(listener, name);
    }

    /**
     *
     * @param name
     */
    protected ArrayTask(String name) {
	super(name);
    }

    @Override public boolean validate() {
	if (taskIndex == tasks.length) {
	    if (taskDebug && true) Log.fine("Just completed all sub tasks for " + name);
	    reset();
	    return true;
	}
	return false;
    }

    @Override public int loop() {
	if (tasks == null) {
	    tasks = createTasks();
	    for(int i = 0; i < tasks.length; i++) {
		if (tasks[i] == null) {
		    throw new IllegalArgumentException("Task is null at index " + i);
		}
		if (tasks[i].getListener() != null && !tasks[i].getListener().equals(this))
		    throw new IllegalArgumentException("Not all child has the ArrayTask as their TaskListener ");
	    }
	}
        return super.loop();
    }

    @Override public int execute() {
	if (taskIndex >= tasks.length || taskIndex < 0)
	    taskIndex = 0;
	return tasks[taskIndex].loop();
    }

    @Override public void onTaskComplete(Task task) {
	if (taskDebug  && true)
	    Log.fine("We completed " + task + " for the subscript " + name);
	taskIndex++;
    }

    @Override public void reset() {
        for (Task task : tasks)
            task.reset();
	super.reset();
	taskIndex = 0;
    }

    public int numberOfTasks() {
        return tasks.length;
    }

    /**
     * Gets called by the constructor once. MUST be tasklistener of all created tasks.
     * @return
     */
    protected abstract Task[] createTasks();
}
