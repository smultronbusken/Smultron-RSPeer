package org.smultron.framework.content.grandexchange;

import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.smultron.framework.MullbarRand;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.SimpleTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;


/**
 * Makes an offer at GE, collects the item and unnotes them.
 * <p>
 * Assumes unlimited coins
 * Assumes unlimited inventory slots
 */
public class BuyCollectUnnote extends ArrayTask {
	private String item;
	private int amount;

	public BuyCollectUnnote(final TaskListener listener, final String item, final int amount) {
		super(listener, "Buying " + amount + " " + item + ", then collecting them.");
		this.item = item;
		this.amount = amount;
	}

	@Override
	protected Task[] createTasks() {
		Task makeGEOffer = MakeGEOfferBuilder.getNewInstance()
				.setOfferType(RSGrandExchangeOffer.Type.BUY)
				.setItem(item)
				.setQuantity(amount)
				.setListener(this)
				.setWaitForCompletion(true)
				.build();

		Task unnote = new SimpleTask(this, "Unnoting the items") {
			@Override
			public int execute() {
				if (Bank.isOpen()) {
					Bank.deposit(item, amount);
					taskDone();
				}
				Bank.open();
				return MullbarRand.nextInt(1000, 2000);
			}
		};

		Task[] tasks = new Task[]{makeGEOffer, unnote};
		return tasks;
	}
}
