package org.smultron.framework.content.grandexchange;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.providers.RSGrandExchangeOffer.Type;
import org.rspeer.ui.Log;

/**
 * Assumes the GrandExchangeSetup is open.
 */
public interface PriceStrategy {
	/**
	 * Gets called by {@link MakeGEOffer} in the final stage of the offer creation.
	 *
	 * @return return value of the interaction
	 */
	public boolean modifyPrice(Type offerType, Item item, int currentPricePerItem);

	/**
	 * Increases buying prices by 9% and decreases selling prices by 12%.
	 */
	public final static PriceStrategy SAFE = new PriceStrategy() {
		@Override
		public boolean modifyPrice(final Type offerType, final Item item, final int currentPricePerItem) {
			if (offerType.equals(Type.SELL)) {
				return GrandExchangeSetup.decreasePrice(4);
			} else if (offerType.equals(Type.BUY)) {
				return GrandExchangeSetup.increasePrice(3);
			}
			Log.severe("Offer is not buy or sell");
			return false;
		}
	};
}
