package org.smultron.framework.thegreatforest;

import org.rspeer.ui.Log;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 *
 */
public class BinaryBranch extends TreeNode {
	private Supplier<TreeNode> successNode = null;
	private BooleanSupplier validation = null;
	private Supplier<TreeNode> failureNode = null;

	/**
	 * @param successNode
	 * @param validate
	 * @param failureNode
	 */
	public BinaryBranch(final Supplier<TreeNode> successNode, final BooleanSupplier validate,
						final Supplier<TreeNode> failureNode) {
		this.successNode = successNode;
		this.validation = validate;
		this.failureNode = failureNode;
	}

	/**
	 *
	 */
	protected BinaryBranch() {
	}

	@Override
	public Iterator<TreeNode> iterator() {
		Iterator<TreeNode> binaryIterator = new Iterator<TreeNode>() {
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public TreeNode next() {
				try {
					if (validation.getAsBoolean()) {
						return successNode.get();
					} else {
						return failureNode.get();
					}
				} catch (NullPointerException e) {
					Log.severe("Could not find the next node. Validation, success node, or failure node was null or threw an NPE");
					return new LeafNode("Wwell, shiet i can t do anyting LMAO");
				}
			}
		};
		return binaryIterator;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	/**
	 * @deprecated BROKEN
	 */
	@Override
	public Task asTask(final TaskListener listener, final String name) {
		TreeNode t = this;
		final Task task = new TreeTask(listener, name) {
			@Override
			public TreeNode onCreateRoot() {
				return t;
			}

			@Override
			public boolean validate() {
				return false;
			}
		};
		return task;
	}

	/*
	Helper method for any inhereting class
	 */
	protected void setSuccessNode(final Supplier<TreeNode> successNode) {
		this.successNode = successNode;
	}

	/*
	Helper method for any inhereting class
	 */
	protected void setFailureNode(final Supplier<TreeNode> failureNode) {
		this.failureNode = failureNode;
	}

	/*
	Helper method for any inhereting class
	 */
	protected void setValidation(final BooleanSupplier validation) {
		this.validation = validation;
	}

	/*
	Builder interfaces
	 */
	public static interface SuccessNode {
		public Validation successNode(final Supplier<TreeNode> successNode);

		public Validation successNode(final TreeNode successNode);

		public Validation successNode(final Task successTask);
	}

	public static interface Validation {
		public FailureNode setValidation(final BooleanSupplier validation);
	}

	public static interface FailureNode {
		public Builder failureNode(final Supplier<TreeNode> failureNode);

		public Builder failureNode(final TreeNode failureNode);

		public Builder failureNode(final Task failureTask);
	}

	public static interface Builder {
		public BinaryBranch build();
	}
}
