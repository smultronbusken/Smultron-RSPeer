package org.smultron.framework.content.item.gathering;

import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.content.Idle;
import org.smultron.framework.content.item.BuyFromStore;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Store;


/**
 * A collection of task which gathers some of the most common items.
 */
public final class GatheringTasks
{
    private GatheringTasks() {
    }

    /**
     * @return a {@link Task} for gethering ONE item of name {@code item}.
     */
    public static Task getTaskFor(TaskListener listener, String item, int amount) {
        switch(item){
            case "Pot":
                return new GatherPot(listener);
            case "Egg":
                return new GatherEgg(listener);
            case "Grain":
                return new GatherWheat(listener);
            case "Shears":
                return new GatherShear(listener);
            case "Spade":
                return new BuyFromStore(listener, "Spade", 1, Store.GENERAL_STORE_LUMBRIDGE);
            case "Bucket":
            case "Hammer":
                return new BuyFromStore(listener, item, amount, Store.GENERAL_STORES.get(0));
            default:
                Log.severe("Sorry, I dont know how to get " + item);
                return new Idle();
        }
    }

    /**
     * Gathers a pot from Lumbridge
     */
    public static class GatherPot extends TreeTask
    {
        public GatherPot(final TaskListener listener) {
    	super(listener, "Getting a pot from the chef in Lumbridge");
        }

        @Override public TreeNode onCreateRoot() {
            Task pickUpPot = new InteractWith<>("Take", () -> Pickables.getNearest("Pot"));
            TreeNode isAtKitchen = new InArea(pickUpPot, CommonLocation.LUMBRIDGE_COOK, 1);
            return isAtKitchen;
        }

        @Override public boolean validate() {
    	return Inventory.contains("Pot");
        }
    }

    /**
     * Walks to Groats Farm in Lumbridge and picks up an egg
     */
    public static class GatherEgg extends ArrayTask
    {
        public GatherEgg(final TaskListener listener) {
    	    super(listener, "Gather egg");
        }

        @Override protected Task[] createTasks() {
            Task moveToFarm = new MoveTo(this, CommonLocation.LUMBRIDGE_GROATS_FARM, 2);
            Task takeEgg = new InteractWith<>("Take", () -> Pickables.getNearest("Egg"));
            takeEgg.attachListener(this);
            Task[] tasks = new Task[]{ moveToFarm, takeEgg };
            return tasks;
        }
    }

    /**
     * Takes a Shear from Fred the Farmer in Lumbridge
     */
    public static class GatherShear extends ArrayTask
    {
        public GatherShear(final TaskListener listener) {
    	super(listener, "Getting a shear from Fred the Farmers house.");
        }

        @Override protected Task[] createTasks() {
    	Task move = new MoveTo(this, CommonLocation.LUMBRIDGE_FREDTHEFARMER, 2);
     	Task pickUp = new InteractWith<>("Take", () -> Pickables.getNearest("Shears"));
     	pickUp.attachListener(this);
     	Task[] tasks = new Task[] { move, pickUp };
    	return tasks;
        }
    }

    /**
     * Picks up Grain in Lumbridge
     */
    public static class GatherWheat extends TreeTask
    {
        public GatherWheat(final TaskListener listener) {
    	super(listener, "");
        }

        @Override public TreeNode onCreateRoot() {
            Task pickUpWheat = new InteractWith<>("Pick", () -> SceneObjects.getNearest("Wheat"));
            TreeNode isAtWheatField = new InArea(pickUpWheat, CommonLocation.LUMBRIDGE_WHEATFIELD, 3);
    	    return isAtWheatField;
        }

        @Override public boolean validate() {
    	    return Inventory.contains("Grain");
        }
    }
}
