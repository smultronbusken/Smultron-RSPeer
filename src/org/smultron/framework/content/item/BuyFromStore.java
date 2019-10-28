package org.smultron.framework.content.item;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.Shop;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.ui.Log;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.info.Store;

/**
 * Assumes unlimited coins
 */
public class BuyFromStore extends TreeTask
{
    private String itemName;
    private int amount;
    private Store store;

    public BuyFromStore(final TaskListener listener, String itemName, int amount, Store store) {
	super(listener, "Buying " + amount + " " + itemName + " from " + store.name());
	this.itemName = itemName;
	this.amount = amount;
	this.store = store;
    }

    @Override public TreeNode onCreateRoot() {
	Task buyItem = new FunctionalTask(() ->
	    Shop.buy(item -> item.getName().equals(itemName), amount)
	);
	Task startTrade = new InteractWith<>("Trade", Npcs::getNearest, store.getNpc());

	TreeNode isShopOpen = BinaryBranchBuilder.getNewInstance()
		.successNode(buyItem)
		.setValidation(Shop::isOpen)
		.failureNode(startTrade)
		.build();
	TreeNode atShop = new InArea(isShopOpen, store, 2);
	return atShop;
    }

    @Override public boolean validate() {
        Item item = Inventory.getFirst(itemName);
        if(item != null) {
	    return Inventory.getCount(true, itemName) >= amount;
	} else {
            Log.severe("I dont have that item.");
	}
	return false;
    }
}
