package org.smultron.framework;

import org.rspeer.runetek.api.Game;
import org.rspeer.script.Script;
import org.smultron.framework.content.banking.BankCache;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;

/**
 *
 */
public abstract class MullbarScript extends Script implements TaskListener {
	private Task task = null;
	public boolean isRunning = true;
	private MullbarGUI gui;

	//TODO
	private boolean turnOfMusic = false;
	private boolean graphics = true;

	public MullbarScript() {
	}

	@Override
	public void onStart() {
		super.onStart();
		Game.getEventDispatcher().register(BankCache.getInstance());
		if (graphics) Game.getEventDispatcher().register(MullbarGraphics.getInstance());
		if (turnOfMusic)
			task = new FunctionalTask(() -> {/*TODO*/});
		gui = createGui();
		if (gui != null)
			gui.open();
	}


	@Override
	public int loop() {
		if (isRunning) {
			if (task == null)
				taskCompleted();
			MullbarGraphics.getInstance().reset();
			return task.loop();
		} else {
			return 100;
		}
	}

	@Override
	public void onStop() {
		Game.getEventDispatcher().deregister(BankCache.getInstance());
		if (graphics) Game.getEventDispatcher().deregister(MullbarGraphics.getInstance());
		super.onStop();
	}


	@Override
	public final void onTaskComplete(Task task) {
		taskCompleted();
	}

	/**
	 * Called once in the {@link MullbarScript} constructor.
	 * Override and return null if no SimpleTutorialIslandGUI is wanted.
	 *
	 * @return your custom {@link MullbarGUI}
	 */
	public MullbarGUI createGui() {
		return new MullbarGUI(this);
	}

	/**
	 * Calls nextTask()
	 */
	public void taskCompleted() {
		task = nextTask();
		task.onStart();
	}

	/**
	 * Called when the script is started and when the current task is completed.
	 *
	 * @return a {@link Task}
	 */
	public abstract Task nextTask();
}
