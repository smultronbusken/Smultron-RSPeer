package org.smultron.scripts.playground;

import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.ScriptMeta;
import org.smultron.framework.Location;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.content.banking.GetItemFromBank;
import org.smultron.framework.content.grandexchange.BuyCollectUnnote;
import org.smultron.framework.content.grandexchange.MakeGEOfferBuilder;
import org.smultron.framework.content.item.GatherItems;
import org.smultron.framework.info.Store;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.MullbarScript;

import java.util.ArrayList;
import java.util.Arrays;

@ScriptMeta(desc = "playing around", developer = "smultron", name = "Playground")
public class 	Playground extends MullbarScript
{

    @Override public Task nextTask() {
        return MakeGEOfferBuilder.getNewInstance()
		.setOfferType(RSGrandExchangeOffer.Type.BUY)
		.setItem("Bucket")
		.setQuantity(1)
		.setWaitForCompletion(true)
		.build();
    }
}
