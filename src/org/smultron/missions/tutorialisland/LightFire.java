package org.smultron.missions.tutorialisland;

import org.rspeer.runetek.adapter.Positionable;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.content.UseItemOn;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;


import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * Assumes tinderbox and logs are in the inventory
 */
public class LightFire extends TreeTask implements TaskListener
{
    private Predicate<? super Item> logPredicate;
    private boolean madeFire = false;

    public LightFire(final TaskListener listener, final Predicate<? super Item> logPredicate)
    {
	super(listener, "Lighting a fire");
	this.logPredicate = logPredicate;
    }

    @Override public boolean validate() {
	return madeFire;
    }

    @Override public void reset() {
	super.reset();
	madeFire = false;
    }

    @Override public TreeNode onCreateRoot() {
        Task makeFire = new UseItemOn<Item>(() -> Inventory.getFirst("Tinderbox"), Inventory::getFirst, logPredicate);
        makeFire.attachListener(this);

	BooleanSupplier standingOnFire = () -> {
	    SceneObject fire = SceneObjects.getFirstAt(Players.getLocal().getPosition());
	    return fire == null || !fire.getName().equals("Fire");
	};

        Task walkToFreeTile = new FunctionalTask(() ->  {
	    Movement.walkTo(Players.getLocal().getPosition().translate(Random.nextInt(-1, 1), Random.nextInt(-1, 1)));
	    Time.sleep(3000);
	}).setName("Cant make fire here.");

        TreeNode canMakeFire = BinaryBranchBuilder.getNewInstance()
		.successNode(makeFire)
		.setValidation(standingOnFire)
		.failureNode(walkToFreeTile)
		.build();

        return canMakeFire;
    }


    @Override public void onTaskComplete(final Task task) {
	madeFire = true;
    }
}
