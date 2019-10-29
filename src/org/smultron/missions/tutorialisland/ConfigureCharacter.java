package org.smultron.missions.tutorialisland;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.component.Interfaces;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;


public class ConfigureCharacter extends TreeTask {
	private String accountName;

	public ConfigureCharacter(String accountName) {
		super("I am customizing my character.");
		this.accountName = accountName;
	}

	@Override
	public TreeNode onCreateRoot() {
		TreeNode shouldEnterName = BinaryBranchBuilder.getNewInstance()
				.successNode(new EnterName(accountName))
				.setValidation(this::enterNameInterfaceExists)
				.failureNode(new GetDressed())
				.build();
		return shouldEnterName;
	}

	@Override
	public boolean validate() {
		return false;
	}

	public boolean enterNameInterfaceExists() {
		InterfaceComponent enterNameComponent = Interfaces.getFirst(558, ic -> ic.getText().equals("Display name"));
		if (enterNameComponent == null || !Interfaces.isVisible(enterNameComponent.toAddress()))
			return false;
		else
			return true;
	}
}
