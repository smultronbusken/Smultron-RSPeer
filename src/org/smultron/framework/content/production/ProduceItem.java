package org.smultron.framework.content.production;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Production;
import org.rspeer.runetek.api.component.Production.Amount;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;

// TODO Rewrite this
public class ProduceItem extends TreeTask {

	private String producer = null;
	private String item;

	public ProduceItem(final TaskListener listener, final String item) {
		super(listener, "Producing " + item);
		this.item = item;
	}

	@Override
	public TreeNode onCreateRoot() {
		Task initiate = new FunctionalTask(() -> {
			Production.initiate();
			Time.sleep(5000, 10000);
		});
		Task setToAll = new FunctionalTask(() -> Production.setAmount(Amount.ALL));

		TreeNode correctQuantity = BinaryBranchBuilder.getNewInstance()
				.successNode(initiate)
				.setValidation(() -> Production.getAmount().equals(Amount.ALL))
				.failureNode(setToAll)
				.build();
		return correctQuantity;
	}

	@Override
	public boolean validate() {
		return false;
	}
}
