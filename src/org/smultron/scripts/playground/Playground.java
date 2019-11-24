package org.smultron.scripts.playground;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;
import org.smultron.framework.MullbarScript;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;

import java.util.function.Predicate;

@ScriptMeta(desc = "playing around", developer = "smultron", name = "Playground")
public class Playground extends MullbarScript {
	@Override
	public Task nextTask() {
		return new FunctionalTask(() -> {
			Predicate<InterfaceComponent> hasAcceptAction = ic -> ic.getToolTip().equals("Accept");
			InterfaceComponent[] acceptButtons = Interfaces.get(hasAcceptAction);
		    Log.fine(acceptButtons.length);
		}).setName("bane");
	}
}
