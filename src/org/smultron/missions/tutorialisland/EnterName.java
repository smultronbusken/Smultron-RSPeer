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

import java.util.function.BooleanSupplier;


public class EnterName extends TreeTask
{
    private String accountName;

    public EnterName(String accountName) {
        super("Entering name");
        this.accountName = accountName;
    }

    @Override public boolean validate() {
	return false;
    }

    @Override public TreeNode onCreateRoot() {
        Task writeName = new FunctionalTask(() -> {
	    InterfaceComponent nameBox = Interfaces.getComponent(558, 7);
	    nameBox.click();
	    Time.sleep(Random.nextInt(1000, 2000));
	    // Write the name and press enter
	    Keyboard.sendText(accountName);
	    Keyboard.pressEnter();
	});
        TreeNode canWriteName = BinaryBranchBuilder.getNewInstance()
		.successNode(isNameAvailable())
		.setValidation(this::haveWrittenName)
		.failureNode(writeName)
		.build();
	return canWriteName;
    }

    public boolean haveWrittenName () {
	InterfaceComponent availableInfo = Interfaces.getComponent(558, 12);
	if(availableInfo != null && availableInfo.getText().contains("<col=00ff00>available</col>"))
	    return true;
	// Have we written the name?
	InterfaceComponent nameBox = Interfaces.getFirst(558, interfaceComponent -> interfaceComponent.getText().equals(
		accountName));
	if (nameBox != null && Interfaces.isVisible(nameBox.toAddress())) return true;
	else return false;
    }

    public TreeNode isNameAvailable() {
	Task pickSuggestedName = new FunctionalTask(() -> {
	    InterfaceComponent suggestedNameButton = Interfaces.getComponent(558, 14);
	    if(suggestedNameButton.click())
	    	suggestedNameButton.click();
	});
        Task setName = new FunctionalTask(() -> {
	    InterfaceComponent setNameButton = Interfaces.getComponent(558, 18);
	    if (setNameButton != null)
	    	setNameButton.click();
	});
	BooleanSupplier isNameTaken = () -> {
	    InterfaceComponent availableNameInterface = Interfaces.getComponent(558, 12);
	    if (availableNameInterface != null && availableNameInterface.getText().contains("not available")) return false;
	    else return true;
	};
        TreeNode isNameAvailable = BinaryBranchBuilder.getNewInstance()
		.successNode(setName)
		.setValidation(isNameTaken)
		.failureNode(pickSuggestedName)
		.build();
        return isNameAvailable;
    }
}
