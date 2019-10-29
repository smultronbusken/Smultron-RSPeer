package org.smultron.scripts.tutorialIsland;

import org.rspeer.runetek.api.Varps;
import org.rspeer.script.ScriptCategory;
import org.rspeer.script.ScriptMeta;
import org.smultron.framework.MullbarGUI;
import org.smultron.framework.MullbarScript;
import org.smultron.framework.content.Idle;
import org.smultron.framework.info.KnownVarp;
import org.smultron.framework.tasks.Task;
import org.smultron.missions.tutorialisland.TutorialIsland;

@ScriptMeta(name = "Smultron Tutorial Island",desc = "Yet another Tutorial Island bot.", developer = "smultron",category = ScriptCategory.QUESTING)
public class SimpleTutorialIsland extends MullbarScript {

	public String accountName = " ";

	@Override
	public MullbarGUI createGui() {
		return new SimpleTutorialIslandGUI(this);
	}

	@Override
	public Task nextTask() {
		if (Varps.getBitValue(KnownVarp.TUTORIAL_ISLAND.getId()) == 1000) {
			setStopping(true);
			return new Idle("I am done");
		}
		return new TutorialIsland(this, accountName);
	}
}
