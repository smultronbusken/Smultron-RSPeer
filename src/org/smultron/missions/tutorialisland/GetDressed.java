package org.smultron.missions.tutorialisland;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.ui.Log;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetDressed extends TreeTask {

	private final List<String> dialog = new ArrayList<>(Arrays.asList(
			"Hmmm, what should i wear...",
			"Does this look any good?",
			"Oh, I like this.",
			"Amazing, wow",
			"Love this one for sure."
	));

	private static final InterfaceComponent[] CUSTOMIZATION_BUTTONS = {
			Interfaces.getComponent(269, 106),
			Interfaces.getComponent(269, 107),
			Interfaces.getComponent(269, 108),
			Interfaces.getComponent(269, 109),
			Interfaces.getComponent(269, 110),
			Interfaces.getComponent(269, 111),
			Interfaces.getComponent(269, 112),
			Interfaces.getComponent(269, 113),
			Interfaces.getComponent(269, 114),
			Interfaces.getComponent(269, 115),
			Interfaces.getComponent(269, 116),
			Interfaces.getComponent(269, 117),
			Interfaces.getComponent(269, 118),
			Interfaces.getComponent(269, 119),
			Interfaces.getComponent(269, 121),
			Interfaces.getComponent(269, 122),
			Interfaces.getComponent(269, 123),
			Interfaces.getComponent(269, 124),
			Interfaces.getComponent(269, 125),
			Interfaces.getComponent(269, 129),
			Interfaces.getComponent(269, 130),
			Interfaces.getComponent(269, 131),
	};

	public GetDressed() {
		super("Getting dressed.");
	}

	@Override
	public TreeNode onCreateRoot() {
		Task customize = new FunctionalTask(() -> {
			InterfaceComponent chosenButton = CUSTOMIZATION_BUTTONS[Random.nextInt(CUSTOMIZATION_BUTTONS.length - 1)];
			if (chosenButton != null) {
				if (Interfaces.isVisible(chosenButton.toAddress()))
					chosenButton.click();
				Log.fine(dialog.get(Random.nextInt(dialog.size())));
			}
		});
		Task beDoneWithIt = new FunctionalTask(() -> {
			InterfaceComponent acceptButton = Interfaces.getComponent(269, 99);
			if (acceptButton != null) {
				acceptButton.click();
			}
		});
		TreeNode shouldGetDressed = BinaryBranchBuilder.getNewInstance()
				.successNode(customize)
				.setValidation(() -> Random.nextInt(20) != 1) // We want on average pick 20 different clothes.
				.failureNode(beDoneWithIt)
				.build();
		return shouldGetDressed;
	}

	@Override
	public boolean validate() {
		return false;
	}
}
