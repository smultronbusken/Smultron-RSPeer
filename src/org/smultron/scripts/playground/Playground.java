package org.smultron.scripts.playground;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;
import org.smultron.framework.Location;
import org.smultron.framework.MullbarScript;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.content.banking.UnbankInventory;
import org.smultron.framework.content.grandexchange.MakeGEOffer;
import org.smultron.framework.content.grandexchange.MakeGEOfferBuilder;
import org.smultron.framework.content.random.ShearSheep;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.quests.cooksassistant.CooksAssistant;
import org.smultron.quests.sheepshearer.ShearSheepAndSell;

@ScriptMeta(desc = "playing around", developer = "smultron", name = "Playground")
public class Playground extends MullbarScript {
	@Override
	public Task nextTask() {
		return new CooksAssistant(this);
	}
}
