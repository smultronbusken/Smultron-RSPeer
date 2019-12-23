package org.smultron.framework.content.dialog;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.component.Dialog;
import org.smultron.framework.content.InteractWith;
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
		setSuccessNode(() -> new LeafNode(ProcessContinue.processContinue()));
		setValidation(() -> Dialog.isOpen() || Game.isInCutscene());
		setFailureNode(() -> new LeafNode(new InteractWith<>("Talk-to", npc)));
	}

}
