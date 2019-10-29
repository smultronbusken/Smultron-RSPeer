package org.smultron.framework.tasks;


/**
 * A {@link SimpleTask} validates only after {@code taskDone()} has been called.
 * This is useful if it is hard coming up with a good validate() implementation.
 */
public abstract class SimpleTask extends Task {
	private boolean done = false;

	/**
	 * @param listener
	 * @param name
	 */
	protected SimpleTask(TaskListener listener, final String name) {
		super(listener, name);
	}

	/**
	 * @param name
	 */
	protected SimpleTask(final String name) {
		super(name);
	}

	@Override
	final public boolean validate() {
		return done;
	}

	@Override
	public void reset() {
		super.reset();
		done = false;
	}

	protected final void taskDone() {
		if (listener != null)
			listener.onTaskComplete(this);
		done = true;
	}
}
