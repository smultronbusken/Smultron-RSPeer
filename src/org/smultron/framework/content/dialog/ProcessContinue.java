package org.smultron.framework.content.dialog;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.ui.Log;
import org.smultron.framework.MullbarRand;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;

public class ProcessContinue {
	static Task processContinue() {
		return new FunctionalTask(() -> {
			if (Dialog.canContinue()) {
				if (!Dialog.processContinue()) {
					Dialog.getContinue().click();
				}
				Time.sleepUntil(() -> !Dialog.isProcessing(), (int) (MullbarRand.getScalar() * 500), (int) (MullbarRand.getScalar() * 1000));
			} else {
				Log.severe("Cant continue");
			}
		}).setName("Continuing dialog...");
	}
}
