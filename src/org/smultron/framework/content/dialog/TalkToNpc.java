package org.smultron.framework.content.dialog;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.ui.Log;
import org.smultron.framework.MullbarRand;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.thegreatforest.BinaryBranch;
import org.smultron.framework.thegreatforest.LeafNode;

import java.util.function.Supplier;

/**
 * Talks to {@link Npc}s. Does not process any options, see {@Link org.smultron.framework.content.dialog.ProcessDialogTree}
 */
public class TalkToNpc extends BinaryBranch {
	/**
	 * @param npc the {@link Npc} it will try to interact with using the action "Talk-To" if the dialoge is not open.
	 */
	public TalkToNpc(Supplier<Npc> npc) {
		super();
		setSuccessNode(() -> new LeafNode(processContinue()));
		setValidation(() -> Dialog.isOpen() || Game.isInCutscene());
		setFailureNode(() -> new LeafNode(new InteractWith<>("Talk-to", npc)));
	}

	private static Task processContinue() {
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
