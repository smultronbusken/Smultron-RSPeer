package org.smultron.quests.sheepshearer;

import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.ui.Log;
import org.smultron.framework.content.grandexchange.MakeGEOfferBuilder;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.content.grandexchange.MakeGEOffer;
import org.smultron.framework.content.random.ShearSheep;

public class ShearSheepAndSell extends ArrayTask
{

    private int amount;

    public ShearSheepAndSell(final TaskListener listener, int amount) {
	super(listener, "I am going to shear " + amount + " sheeps and then sell them att GE.");
	this.amount = amount;
    }

    @Override protected Task[] createTasks() {
	Task shear = new ShearSheep(amount, this);
	Task[] tasks = new Task[]{
		shear,
		MakeGEOfferBuilder.getNewInstance()
		    .setOfferType(RSGrandExchangeOffer.Type.SELL)
		    .setItem("Wool")
		    .setQuantity(MakeGEOffer.ALL)
		    .setWaitForCompletion(true)
		    .setListener(this)
		    .build()
	};
	return tasks;
    }

    @Override public void reset() {
	Log.info("Reseting");
        super.reset();
    }
}
