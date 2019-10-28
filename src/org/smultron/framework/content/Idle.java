package org.smultron.framework.content;

import org.smultron.framework.MullbarRand;
import org.smultron.framework.tasks.Task;

/**
 * A task which does nothing.
 */
public class Idle extends Task
{
    public Idle() {
	super("Chilling");
    }

    public Idle(String name) {
	super(name);
    }

    @Override public boolean validate() {
	return false;
    }

    @Override public int execute() {
	return MullbarRand.nextInt(200, 1200);
    }
}
