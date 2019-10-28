package org.smultron.framework.content.banking;

import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranch;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;


public class UnbankInventory extends TreeTask
{
    public UnbankInventory(final TaskListener listener) {
	super(listener, "Unbanking inventory");
    }

    @Override public TreeNode onCreateRoot() {
        Task openbank = new FunctionalTask(Bank::open);
        Task unbankAndNotify = new FunctionalTask(Inventory::isEmpty, Bank::depositInventory);
	BinaryBranch isBankOpen = BinaryBranchBuilder.getNewInstance()
		.successNode(unbankAndNotify)
		.setValidation(Bank::isOpen)
		.failureNode(openbank)
		.build();
        return isBankOpen;
    }

    @Override public boolean validate() {
	return Inventory.isEmpty();
    }


}
