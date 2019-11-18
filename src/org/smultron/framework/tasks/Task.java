package org.smultron.framework.tasks;

import org.rspeer.ui.Log;
import org.smultron.framework.MullbarGraphics;
import org.smultron.framework.MullbarRand;


/**
 * Base class for all types of tasks.
 * Call {@code loop()} to run the Task once.
 */
public abstract class Task {

	/**
	 * Set to true if ALL tasks should be printing debug.
	 */
	public static boolean taskDebug = true;
	protected TaskListener listener = null; // TODO Make a list of listeners?
	protected String name;
	protected String status = "";

	/**
	 * @param listener
	 * @param name     Name of the task
	 */
	public Task(final TaskListener listener, final String name) {
		if (taskDebug)
			Log.info("Creating task " + name);
		this.listener = listener;
		this.name = name;
	}

	/**
	 * @param name Name of the task
	 */
	public Task(final String name) {
		if (taskDebug)
			Log.info("Creating task " + name);
		this.name = name;
	}

	@Override
	public String toString() {
		return status != null && !status.isEmpty() ? name + status : name;
	}

	protected void notifyListener() {
		if (listener != null)
			listener.onTaskComplete(this);
	}

	public void reset() {
		if (taskDebug) Log.info("Reseting task " + name);
	}

	/**
	 * When a {@SmartyScript SmartyScript} attaches a Task, {@code onStart()} gets called.
	 */
	public void onStart() {
		if (taskDebug) Log.fine("Starting task " + name);
	}

	/**
	 * When this returns true, the TaskListener is notified.
	 *
	 * @return True if the task is complete
	 */
	public abstract boolean validate();

	/**
	 * Put your logic in this method.
	 *
	 * @return Sleep time
	 */
	public abstract int execute();

	/**
	 * Calls {@code validate()} and if false, calls thereafter {@code execute()}.
	 * If {@code validate()} returns true, notify the task's {@link TaskListener}.
	 * <p>
	 * Use {@code loop()} in every case where {@code validate()} and {@code execute()} must not be called independently.
	 *
	 * @return {@code execute()} or an int between 700 - 1400
	 */
	public int loop() {
		MullbarGraphics.getInstance().queue(toString());
		if (taskDebug) Log.info("Doing: " + name + "," + (status != null ? name + status : name));
		if (validate()) {
			notifyListener();
			return MullbarRand.nextInt(700, 1400);
		}
		return execute();
	}

	public void attachListener(TaskListener listener) {
		this.listener = listener;
	}

	public TaskListener getListener() {
		return listener;
	}

	public Task setName(String name) {
		this.name = name;
		return this;
	}
}
