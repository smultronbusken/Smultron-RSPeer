package org.smultron.framework.content.grandexchange;

import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSItemDefinition;
import org.smultron.framework.content.grandexchange.MakeGEOffer.*;
import org.smultron.framework.tasks.TaskListener;

import java.util.function.Predicate;

/**
 * Builder class for {@link MakeGEOffer}.
 */
public class MakeGEOfferBuilder implements ItemSetter, OfferTypeSetter, QuantitySetter, Builder
{

    private PriceStrategy priceStrategy = PriceStrategy.SAFE;
    private boolean waitForCompletion = false;
    private TaskListener listener = null;
    private RSGrandExchangeOffer.Type offerType;
    private String item;
    private int quantity;

    private MakeGEOfferBuilder(){
    }

    public static OfferTypeSetter getNewInstance(){
    	return new MakeGEOfferBuilder();
    }

    @Override public ItemSetter setOfferType(final RSGrandExchangeOffer.Type type) {
        this.offerType = type;
	return this;
    }

    @Override public QuantitySetter setItem(final String item) {
        this.item = item;
	return this;
    }

    @Override public Builder setQuantity(final int quantity) {
        this.quantity = quantity;
	return this;
    }

    @Override public Builder setPriceStrategy(final PriceStrategy priceStrategy) {
        this.priceStrategy = priceStrategy;
	return this;
    }

    @Override public Builder setWaitForCompletion(final boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
	return this;
    }

    @Override public Builder setListener(final TaskListener listener) {
	this.listener = listener;
	return this;
    }

    @Override public MakeGEOffer build() {
	return new MakeGEOffer(listener, item, offerType, quantity, waitForCompletion, priceStrategy);
    }
}
