package org.smultron.framework.thegreatforest;

import org.smultron.framework.tasks.Task;
import org.smultron.framework.thegreatforest.BinaryBranch.Builder;
import org.smultron.framework.thegreatforest.BinaryBranch.FailureNode;
import org.smultron.framework.thegreatforest.BinaryBranch.SuccessNode;
import org.smultron.framework.thegreatforest.BinaryBranch.Validation;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Helper class for building {@link BinaryBranch} objects.
 */
public final class BinaryBranchBuilder implements SuccessNode, Validation, FailureNode, Builder {
	private Supplier<TreeNode> successNode = null;
	private BooleanSupplier validation = null;
	private Supplier<TreeNode> failureNode = null;

	private BinaryBranchBuilder() {
	}

	public static SuccessNode getNewInstance() {
		return new BinaryBranchBuilder();
	}

	public Validation successNode(final Supplier<TreeNode> successNode) {
		this.successNode = successNode;
		return this;
	}

	public Validation successNode(final TreeNode successNode) {
		this.successNode = () -> successNode;
		return this;
	}

	public Validation successNode(final Task successTask) {
		TreeNode leaf = new LeafNode(successTask);
		this.successNode = () -> leaf;
		return this;
	}

	public BinaryBranchBuilder failureNode(final Supplier<TreeNode> failureNode) {
		this.failureNode = failureNode;
		return this;
	}

	public BinaryBranchBuilder failureNode(final TreeNode failureNode) {
		this.failureNode = () -> failureNode;
		return this;
	}

	public BinaryBranchBuilder failureNode(final Task failureTask) {
		TreeNode leaf = new LeafNode(failureTask);
		this.failureNode = () -> leaf;
		return this;
	}

	public FailureNode setValidation(final BooleanSupplier validation) {
		this.validation = validation;
		return this;
	}

	public BinaryBranch build() {
		return new BinaryBranch(successNode, validation, failureNode);
	}
}
