package org.smultron.framework.tasks;

import org.smultron.framework.MullbarRand;

import java.util.function.BooleanSupplier;

/**
 * The most simple but very convenient {@link Task}
 */
public class FunctionalTask extends Task {
	private Runnable execute;
	private BooleanSupplier validate;

	/**
	 * @param validate
	 * @param execute
	 */
	public FunctionalTask(final Runnable execute, final BooleanSupplier validate) {
		super(null, "");
		this.execute = execute;
		this.validate = validate;
	}

	/**
	 * Will always validate {@code false}
	 */
	public FunctionalTask(final Runnable execute) {
		super(null, "");
		this.execute = execute;
		this.validate = () -> false;
	}

	@Override
	public boolean validate() {
		return validate.getAsBoolean();
	}

	@Override
	public int execute() {
		execute.run();
		return MullbarRand.nextInt(600, 1200);
	}
}
