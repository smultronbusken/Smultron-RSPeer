package org.smultron.framework.content.banking;

import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Bank.WithdrawMode;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;


/**
 * Assumes unlimited inventory slots
 * Does not handle stackable items
 */
public class GetItemFromBank extends TreeTask {
	private String item;
	private int amount;
	private WithdrawMode mode;
	private boolean withdrawAll = false;

	public static boolean bankDebug = true;

	/**
	 *
	 */
	public GetItemFromBank(final TaskListener listener, final String item, WithdrawMode mode, boolean withdrawAll) {
		super(listener, "Retrieving " + item + " from bank");
		this.item = item;
		this.mode = mode;
		this.withdrawAll = true;
		;
	}

	/**
	 * @param amount Must be > 0
	 */
	public GetItemFromBank(final TaskListener listener, final String item, WithdrawMode mode, final int amount) {
		super(listener, "Retrieving " + item + " from bank");
		this.item = item;
		this.amount = amount;
		this.mode = mode;
	}

	@Override
	public TreeNode onCreateRoot() {
		Task withdrawItems = new FunctionalTask(() -> {
			if (Bank.contains(i -> i.getName().equals(item))) {
				if (withdrawAll) {
					Bank.withdrawAll(item);
				} else if (Bank.getCount(item) >= amount) {
					Bank.withdraw(item, amount);
				}
			}
		}).setName("Withdrawing items...");
		Task setWithdrawMode = new FunctionalTask(() -> Bank.setWithdrawMode(mode)).setName("Setting withdraw mode");
		Task openbank = new FunctionalTask(Bank::open);

		TreeNode correctWithdrawMode = BinaryBranchBuilder.getNewInstance()
				.successNode(withdrawItems)
				.setValidation(() -> Bank.getWithdrawMode().equals(mode))
				.failureNode(setWithdrawMode)
				.build();

		TreeNode isBankOpen = BinaryBranchBuilder.getNewInstance()
				.successNode(correctWithdrawMode)
				.setValidation(Bank::isOpen)
				.failureNode(openbank)
				.build();

		return isBankOpen;
	}

	/*
	TODO
	 Refractor this SHTTY method
	  Refractor this SHTTY method
	 */
	@Override
	public boolean validate() {
		if (Bank.isOpen() && Bank.isEmpty()) {
			// If the bank is empty the BankCache doesnt get notified and we must manually set it.
			if (bankDebug) Log.fine("My bank is empty");
			BankCache.getInstance().setToEmpty();
			return true;
		}

		if (BankCache.getInstance().mustCheckBank()) {
			if (bankDebug) Log.fine("I need to check how my bank looks like.");
			return false;
		}

		if (withdrawAll) {
			if (BankCache.getInstance().contains(item)) {
				if (bankDebug) Log.fine("There are more " + item + "(s) in the bank.");
				return false;
			} else {
				if (bankDebug) Log.fine("I have all my " + item + "(s).");
				return true;
			}
		} else {
			if (Inventory.getCount(true, item) >= amount) {
				if (BankCache.getInstance().contains(item)) {
					if (bankDebug) Log.fine("They are in the bank. Lets withdraw them. Returning false");
					return false;
				} else {
					if (bankDebug)
						Log.severe("I cant get " + (amount - Inventory.getCount(true, item)) + " " + item + "(s) from the bank.");
					return true;
				}
			} else {
				if (bankDebug) Log.fine("We have the correct amount of items in our inventory already.");
				return true;
			}
		}
	}
}
