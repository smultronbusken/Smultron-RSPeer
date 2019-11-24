package org.smultron.missions.tutorialisland;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.input.Keyboard;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;


public class EnterName extends TreeTask {
	private String accountName;

	public EnterName(String accountName) {
		super("Entering name");
		this.accountName = accountName;
	}

	@Override
	public boolean validate() {
		return false;
	}

	@Override
	public TreeNode onCreateRoot() {
		Task writeName = new FunctionalTask(() -> {
		    Predicate<InterfaceComponent> hasNameAction = ic -> Arrays.asList(ic.getActions()).contains("Look up name");
		    InterfaceComponent[] enterNameButtons = Interfaces.get(hasNameAction);
		    if(enterNameButtons.length > 0) {
				if (enterNameButtons[0].click()) {
					// Write the name and press enter
					Keyboard.sendText(accountName);
					Keyboard.pressEnter();
					Time.sleep(Random.nextInt(1000, 2000));
				}
			}
		});

		Task setName = new FunctionalTask(() -> {
			// See if the "confirm name" button exists.
			Predicate<InterfaceComponent> confirmName = ic -> Arrays.asList(ic.getActions()).contains("Set name") &&
														  ic.getText().isEmpty();
			InterfaceComponent[] confirmNameButton = Interfaces.get(confirmName);
			if(confirmNameButton.length > 0) {
				confirmNameButton[0].click();
				Time.sleep(Random.nextInt(1000, 2000));
				return;
			}
			// Otherwise, we must pick a suggested name
			Predicate<InterfaceComponent> hasSetNameAction = ic -> Arrays.asList(ic.getActions()).contains("Set name");
			InterfaceComponent[] setNameButtons = Interfaces.get(hasSetNameAction);
			if(setNameButtons.length > 0) {
				setNameButtons[0].click();
				Time.sleep(Random.nextInt(1000, 2000));
			}
		});

		TreeNode canWriteName = BinaryBranchBuilder.getNewInstance()
				.successNode(setName)
				.setValidation(this::haveWrittenName)
				.failureNode(writeName)
				.build();
		return canWriteName;
	}

	public boolean haveWrittenName() {
		Predicate<InterfaceComponent> hasNotWrittenOrError = ic -> {
			return ic.getText().contains("Please enter a name") || ic.getText().contains("Please try again");
		};
	    return Interfaces.get(hasNotWrittenOrError).length == 0;
	}
}
