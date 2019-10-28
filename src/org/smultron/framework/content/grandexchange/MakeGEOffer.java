package org.smultron.framework.content.grandexchange;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Progress;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Type;
import org.rspeer.ui.Log;
import org.smultron.framework.MullbarRand;
import org.smultron.framework.content.banking.GetItemFromBank;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.content.Idle;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

import java.util.function.BooleanSupplier;

/**
 * Places a offer at GE amd collects it.
 *
 * Assumes unlimited coins
 * Assumes unlimited inventory slots
 */
public class MakeGEOffer extends TreeTask
{
    private String itemName;
    private int quantity;
    private boolean madeOffer = false;
    private Type offerType;
    private boolean waitForOfferComplete;
    private int offerSlotIndex;
    private PriceStrategy priceStrategy = PriceStrategy.SAFE;

    /*
    Some better way?
     */
    public static final int ALL = 2147483646;


    /**
     * @param offerType Specify if this is a buy or sell offer.
     * @param waitForOfferComplete If set to true, the task will wait for the offer to be completed and then collect it
     */
    public MakeGEOffer(final TaskListener listener, String itemName, Type offerType, int quantity, boolean waitForOfferComplete, PriceStrategy priceStrategy) {
	super(listener, "Making a " +  offerType.name().toLowerCase() + " offer for " + quantity + " " + itemName + " on GE!");
	this.itemName = itemName;
	this.offerType = offerType;
	this.quantity = quantity;
	this.waitForOfferComplete = waitForOfferComplete;
	if(priceStrategy != null)
	    this.priceStrategy = priceStrategy;
    }

    @Override public boolean validate() {
        if (waitForOfferComplete) {
            if(madeOffer) {
		RSGrandExchangeOffer[] offers = GrandExchange.getOffers(offer -> offer.getProgress().equals(Progress.FINISHED));
		boolean offersInProgress = (offers != null && offers.length == 0);
		return offersInProgress;
	    }
            return false;
	} else {
	    return madeOffer;
	}
    }

    @Override public void reset() {
	super.reset();
	madeOffer = false;
    }

    @Override public TreeNode onCreateRoot() {
	Task setQuantity = new FunctionalTask(() -> GrandExchangeSetup.setQuantity(quantity))
		.setName("Configuring quantity...");
        Task setItem = new FunctionalTask(() -> GrandExchangeSetup.setItem(itemName))
		.setName("Configuring item..");
	Task openSetupTab = openSetupTabTask();
	Task confirmOffer = confirmOfferTask();

	// Set quantity
	TreeNode setQuantityAndConfirm = BinaryBranchBuilder.getNewInstance()
		.successNode(confirmOffer)
		.setValidation(() -> GrandExchangeSetup.getQuantity() == quantity)
		.failureNode(setQuantity)
		.build();

	// Set item
	TreeNode isItemCorrect = BinaryBranchBuilder.getNewInstance()
		.successNode(setQuantityAndConfirm)
		.setValidation(() -> GrandExchangeSetup.getItem() != null && GrandExchangeSetup.getItem().getName().equals(itemName))
		.failureNode(setItem)
		.build();

	// Open setup tab
        TreeNode isSetupTabOpen =  BinaryBranchBuilder.getNewInstance()
		.successNode(isItemCorrect)
		.setValidation(GrandExchangeSetup::isOpen)
		.failureNode(openSetupTab)
		.build();

        // Wait and collect offers
	BooleanSupplier shouldCollect = () -> {
	    RSGrandExchangeOffer[] offers = GrandExchange.getOffers(offer -> offer.getProgress().equals(Progress.FINISHED));
	    boolean offersInProgress = (offers != null && offers.length > 0);
	    // Should only return true after we have made a offer
	    return waitForOfferComplete && madeOffer && offersInProgress;
	};
        TreeNode shouldWaitForCompletion = BinaryBranchBuilder.getNewInstance()
		.successNode(new FunctionalTask(GrandExchange::collectAll))
		.setValidation(shouldCollect)
		.failureNode(isSetupTabOpen)
		.build();

        // Open GE
	TreeNode isGEOpen = BinaryBranchBuilder.getNewInstance()
		.successNode(shouldWaitForCompletion)
		.setValidation(GrandExchange::isOpen)
		.failureNode(new FunctionalTask(GrandExchange::open))
		.build();
	TreeNode doPrerequisites = prerequisite(isGEOpen);
	TreeNode isAtGE = new InArea(doPrerequisites, CommonLocation.GE, 5);
	return isAtGE;
    }

    private Task confirmOfferTask() {
	return new FunctionalTask(() -> {
	    Type type = GrandExchangeSetup.getSetupType();
	    Item item = GrandExchangeSetup.getItem();
	    int pricePerItem = GrandExchangeSetup.getPricePerItem();
	    if(Time.sleepUntil(() -> priceStrategy.modifyPrice(type, item, pricePerItem), 10000)){
	        // The PriceStrategy was successful
		if (GrandExchangeSetup.confirm()) {
		    madeOffer = true;
		    Time.sleep(MullbarRand.nextInt(1000, 3000));
		} else {
		    Log.severe("Cant press the confirm button for some reason");
		}
	    }
	}).setName("Confirming offer...");
    }

    private Task openSetupTabTask() {
	return new FunctionalTask(() -> {
	    RSGrandExchangeOffer emptySlot = GrandExchange.getFirstEmpty();
	    if (emptySlot != null){
		if(!GrandExchange.createOffer(offerType)) {
		    Log.severe("Couldnt place offer for some reason");
		}
	    } else {
		Log.severe("No slot available for offer");
	    }
	}).setName("Opening the offer tab");
    }

    /*
    If it is a buy offer: Get all coins from the bank
    If it is a sell offer: Get all the items we are selling from the bank
     */
    private TreeNode prerequisite(TreeNode startMakingOffer) {
	if (offerType.equals(Type.BUY)) {
	    Task getFromBank = new GetItemFromBank(null, "Coins", Bank.WithdrawMode.ITEM, true);
	    return BinaryBranchBuilder.getNewInstance()
		    .successNode(startMakingOffer)
		    .setValidation(getFromBank::validate)
		    .failureNode(getFromBank)
		    .build();
	} else if (offerType.equals(Type.SELL)) {
	    Task getItems = new GetItemFromBank(null, itemName, Bank.WithdrawMode.NOTE, quantity);
	    return BinaryBranchBuilder.getNewInstance()
		    .successNode(startMakingOffer)
		    .setValidation(getItems::validate)
		    .failureNode(getItems)
		    .build();
	}
	Log.severe("I cant make offer: " + offerType);
	return null;
    }

    /*
    Builder interfaces
     */
    public interface OfferTypeSetter
    {
	public ItemSetter setOfferType(Type type);
    }

    public interface ItemSetter
    {
        public QuantitySetter setItem(String item);
	//TODO: public QuantitySetter setItem(int id);
	//TODO: public QuantitySetter setItem(String item, Predicate<RSItemDefinition> predicate);
    }

    public interface QuantitySetter
    {
	public Builder setQuantity(int quantity);
    }

    public interface Builder {
	public Builder setWaitForCompletion(boolean waitForCompletion);
	public Builder setListener(TaskListener listener);
	public Builder setPriceStrategy(PriceStrategy priceStrategy);
        public MakeGEOffer build();
    }
}
