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
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GetDressed extends TreeTask {

	private final List<String> dialog = new ArrayList<>(Arrays.asList(
			"Hmmm, what should i wear...",
			"Does this look any good?",
			"Oh, I like this.",
			"Amazing, wow",
			"Love this one for sure."
	));

	private static final Supplier<InterfaceComponent> CUSTOMIZATION_BUTTONS = () -> {
	    Predicate<InterfaceComponent> p = ic -> ic.getToolTip().contains("Change") || ic.getToolTip().contains("Recolour");
	    InterfaceComponent[] customizationButtons =  Interfaces.get(p);
	    int l = customizationButtons.length;
	    return customizationButtons[Random.nextInt(0, l)];
	};

	public GetDressed() {
		super("Getting dressed.");
	}

	@Override
	public TreeNode onCreateRoot() {
		Task customize = new FunctionalTask(() -> {
			InterfaceComponent chosenButton = CUSTOMIZATION_BUTTONS.get();
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
