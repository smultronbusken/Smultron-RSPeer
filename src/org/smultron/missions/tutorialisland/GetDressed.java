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

	public GetDressed() {
		super("Getting dressed.");
	}

	private static InterfaceComponent randomApperanceButton() {
	    Predicate<InterfaceComponent> p = ic -> ic.getToolTip().contains("Change") || ic.getToolTip().contains("Recolour");
	    InterfaceComponent[] customizationButtons =  Interfaces.get(p);
	    int l = customizationButtons.length;
	    return customizationButtons[Random.nextInt(0, l)];
	}

	@Override
	public TreeNode onCreateRoot() {
		Task customize = new FunctionalTask(() -> {
			InterfaceComponent chosenButton = randomApperanceButton();
			if (chosenButton != null) {
				if (Interfaces.isVisible(chosenButton.toAddress()))
					chosenButton.click();
				Log.fine(dialog.get(Random.nextInt(dialog.size())));
			}
		});
		Task beDoneWithIt = new FunctionalTask(() -> {
			Predicate<InterfaceComponent> hasAcceptAction = ic -> ic.getToolTip().equals("Accept");
			InterfaceComponent[] acceptButtons = Interfaces.get(hasAcceptAction);
			if (acceptButtons.length > 0) {
				acceptButtons[0].click();
			}
		});
		TreeNode shouldGetDressed = BinaryBranchBuilder.getNewInstance()
				.successNode(customize)
				.setValidation(() -> Random.nextInt(12) != 1) // We want on average pick 12 different clothes.
				.failureNode(beDoneWithIt)
				.build();
		return shouldGetDressed;
	}

	@Override
	public boolean validate() {
		return false;
	}
}
