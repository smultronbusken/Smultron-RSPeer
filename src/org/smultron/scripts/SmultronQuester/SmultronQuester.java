package org.smultron.scripts.SmultronQuester;

import org.rspeer.runetek.api.Varps;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;
import org.smultron.framework.MullbarScript;
import org.smultron.framework.info.KnownVarp;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.missions.tutorialisland.TutorialIsland;
import org.smultron.quests.cooksassistant.CooksAssistant;
import org.smultron.quests.doricsquest.DoricsQuest;
import org.smultron.quests.impcatcher.ImpCatcher;
import org.smultron.quests.restlessghost.RestlessGhost;
import org.smultron.quests.romeoandjuliet.RomeoAndJuliet;
import org.smultron.quests.sheepshearer.SheepShearer;
import org.smultron.quests.xmarksthespot.XMarksTheSpot;

@ScriptMeta(developer = "smultron",
		category = ScriptCategory.QUESTING,
		desc = "Completes X Marks the Spot, Cooks Assistant, Romeo & Juliet," +
				"Dorics Quest, The Restless Ghost, and Sheep Shearer. ",
		name = "Smultron F2P Quester",
		version = 1.0)
public class SmultronQuester extends MullbarScript {
	public String accountName = "anon";

	@Override
	public Task nextTask() {
		if (Varps.get(KnownVarp.TUTORIAL_ISLAND.getId()) != 1000) {
			return new TutorialIsland(this, accountName);
		}

		// Find the first quest which arent completed.
		if (Varps.get(Quest.X_MARKS_THE_SPOT.getVarpbit()) < 7)
			// For some reason the varpbit doesnt stay on a value after completing the quest
			// But it does seem to always stay over 10
			return new XMarksTheSpot(this);
		else if (Varps.get(Quest.COOKS_ASSISTANT.getVarpbit()) != Quest.COOKS_ASSISTANT.getStages())
			return new CooksAssistant(this);
		else if (Varps.get(Quest.ROMEO_AND_JULIET.getVarpbit()) != Quest.ROMEO_AND_JULIET.getStages())
			return new RomeoAndJuliet(this);
		else if (Varps.get(Quest.DORICS_QUEST.getVarpbit()) != Quest.DORICS_QUEST.getStages())
			return new DoricsQuest(this);
		else if (Varps.get(Quest.IMP_CATCHER.getVarpbit()) != Quest.IMP_CATCHER.getStages())
			return new ImpCatcher(this);
		else if (Varps.get(Quest.THE_RESTLESS_GHOST.getVarpbit()) != Quest.THE_RESTLESS_GHOST.getStages())
			return new RestlessGhost(this);
		else if (Varps.get(Quest.SHEEP_SHEARER.getVarpbit()) != Quest.SHEEP_SHEARER.getStages())
			return new SheepShearer(this);

		// WE have completed every quest
		return new FunctionalTask(() -> setStopping(true));
	}



}
