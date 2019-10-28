package org.smultron.framework.content;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Identifiable;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.ui.Log;
import org.smultron.framework.MullbarRand;
import org.smultron.framework.tasks.SimpleTask;
import org.smultron.framework.tasks.TaskListener;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Uses an {@link Item} on a {@link T}
 * @param <T> A generic which implements {@link Identifiable} and {@link Interactable}
 */
public class UseItemOn<T extends Identifiable & Interactable> extends SimpleTask
{
    private Supplier<T> targetSupplier;
    private Supplier<Item> itemSupplier;

    public UseItemOn(final Supplier<Item> itemSupplier,  final Supplier<T> targetSupplier)
    {
	super("Using item on target");
	this.targetSupplier = targetSupplier;
	this.itemSupplier = itemSupplier;
    }

    public UseItemOn(final Supplier<Item> itemSupplier, Function<Predicate<? super T>, T> getMethod,
					     Predicate<? super T> predicate)
    {
	super(null, "Using item on target");
	this.targetSupplier = () -> getMethod.apply(predicate);;
	this.itemSupplier = itemSupplier;
    }

    @Override public int execute() {
	Item item = itemSupplier.get();
	T target = targetSupplier.get();

	if(target == null && item != null) {
	    Log.severe("I have " + item.getName() + " but I cant find the thing i want to use it on");
	    return MullbarRand.nextInt(600, 1200);
	}
	if(target != null && item == null) {
	    Log.severe("I dont have the item but I can find " + target.getName());
	    return MullbarRand.nextInt(600, 1200);
	}
	if(target == null && item == null) {
	    Log.severe("I cant neither find the item I want to use or the target.");
	    return MullbarRand.nextInt(600, 1200);
	}
	// We're good to go.
	name = "Using " + item.getName() + " on " + target.getName();
	int threshold = (int)(1000 * MullbarRand.getScalar());	// Declare this for convenience's sake
	int timeout = (int)(4000 * MullbarRand.getScalar());	// Declare this for convenience's sake
	if (Time.sleepUntil(() -> item.interact("Use"), threshold, timeout)){
	    if (Time.sleepUntil(Inventory::isItemSelected, threshold, timeout)){
		if (Time.sleepUntil(() -> target.interact("Use") && !Players.getLocal().isAnimating(), threshold, timeout)) {
		    taskDone();
		    return MullbarRand.nextInt(300, 1200);
		} else {
		    Log.severe("For some reason, i couldnt use " + item.getName() + " on " + target);
		}
	    } else {
		Log.severe(" I clicked with " + item.getName() + " but no item is selected?");
	    }
	} else {
	    Log.severe("For some reason, i couldnt select " + item.getName());
	}
	return MullbarRand.nextInt(600, 1200);
    }
}
