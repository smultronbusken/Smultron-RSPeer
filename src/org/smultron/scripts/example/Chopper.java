package org.smultron.scripts.example;

import org.rspeer.script.ScriptMeta;
import org.smultron.framework.MullbarScript;
import org.smultron.framework.tasks.Task;

@ScriptMeta(name = "Chopper", developer = "smultron", desc = "Chops trees in lumbridge")
public class Chopper extends MullbarScript {
	@Override
	public Task nextTask() {
		return new ChopAndBank();
	}
}
