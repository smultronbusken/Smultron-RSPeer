package org.smultron.framework.content;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;
import org.smultron.framework.MullbarRand;
import org.smultron.framework.tasks.SimpleTask;
import org.smultron.framework.tasks.TaskListener;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * Will interact with a {@link T}
 * @param <T> A type which implements {@link Identifiable} and {@link Interactable}
 */
public class InteractWith<T extends Identifiable & Interactable> extends SimpleTask
{
    private String action;
    private Supplier<T> supplier;

    /**
     *
     */
    public InteractWith(final String action,
			Function<Predicate<? super T>, T> getMethod,
			Predicate<? super T> predicate) {
	super(null, "Doing " + action);
	this.action = action;
	this.supplier = () -> getMethod.apply(predicate);
    }

    /**
     *
     */
    public InteractWith(final String action,
			final Supplier<T> supplier) {
	super(null, "Doing " + action);
	this.action = action;
	this.supplier = supplier;
    }

    @Override public int execute() {
	T target = supplier.get();
	if(target != null ){
	    String[] actions = target.getActions();
	    for(int i = 0; i < actions.length; i++) {
		if (actions[i].equals(action)) {
		    if(target.interact(action)) {
			Time.sleepUntil(() -> !Players.getLocal().isAnimating() && !Players.getLocal().isMoving(), 1000, 8000);
			taskDone();
			return MullbarRand.nextInt(1000, 5000);
		    }
		    Log.severe("Could not " + action + " on " + target.getName());
		}
	    }
	    Log.severe(target.getName() + " has no action " + action);
	} else {
	    Log.severe("Could not find the thing to " + action );
	}
	return MullbarRand.nextInt(1000, 2000);
    }
}
