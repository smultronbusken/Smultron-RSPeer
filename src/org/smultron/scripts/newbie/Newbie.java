package org.smultron.scripts.newbie;

import org.rspeer.runetek.api.Varps;
import org.rspeer.script.ScriptMeta;
import org.smultron.framework.MullbarGUI;
import org.smultron.framework.MullbarScript;
import org.smultron.framework.info.KnownVarp;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.missions.tutorialisland.TutorialIsland;
import org.smultron.quests.cooksassistant.CooksAssistant;
import org.smultron.quests.romeoandjuliet.RomeoAndJuliet;
import org.smultron.quests.xmarksthespot.XMarksTheSpot;

@ScriptMeta(developer = "smultron",
		desc = "Can be started anytime. No prerequisites. Completes Tutorial Island, Cooks Assistant, Dorics Quest, " +
				"Imp Catcher, Restless Ghost, Romer and Juliet, Sheep Shearer, and X Marks the Spot. " +
				"It will shear sheep for money if it does not have over 3000gp in order to buy items for quests.",
		name = "NewbieBot: 1-click Tutorial Island + 11 quest point")
public class Newbie extends MullbarScript {
	//public boolean shearSheepForMoney = false;
	public String accountName = "anon";
	private final int coins = 1000;

	@Override
	public Task nextTask() {
		if (Varps.get(KnownVarp.TUTORIAL_ISLAND.getId()) != 1000) {
			return new TutorialIsland(this, accountName);
		}
		Task quest;
		if (Varps.get(Quest.COOKS_ASSISTANT.getVarpbit()) != Quest.COOKS_ASSISTANT.getStages())
			return new CooksAssistant(this);
		else if (Varps.get(Quest.ROMEO_AND_JULIET.getVarpbit()) != Quest.ROMEO_AND_JULIET.getStages())
			return new RomeoAndJuliet(this);
		else if (Varps.get(Quest.X_MARKS_THE_SPOT.getVarpbit()) < 10)
			return new XMarksTheSpot(this);
		else
			quest = new FunctionalTask(() -> setStopping(true));
		return quest;
	}

	@Override
	public MullbarGUI createGui() {
		return new NewbieGUI(this);
	}
}
