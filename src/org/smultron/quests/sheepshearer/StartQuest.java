package org.smultron.quests.sheepshearer;

import org.smultron.framework.SubScript;
import org.smultron.framework.Task;
import org.smultron.framework.TaskListener;
import org.smultron.framework.tasks.dialog.DoDialogTree;
import org.smultron.framework.tasks.movement.MoveTo;
import org.smultron.info.Location;

import java.util.ArrayDeque;
import java.util.Deque;

class StartQuest extends SubScript
{
    StartQuest(final TaskListener listener) {
	super(listener, "Going to talk to fred the farmer");
    }

    @Override protected Task[] createTasks() {
        Task[] tasks = new Task[2];
        tasks[0] = new MoveTo(this, Location.LUMBRIDGE_FREDTHEFARMER, 2);
	Deque<String> dialogOptions = new ArrayDeque<>();
	dialogOptions.addLast("I'm looking for a quest.");
	dialogOptions.addLast("Yes okay. I can do that.");
	tasks[1] = new DoDialogTree(dialogOptions, "Fred the Farmer").asTask(null, "Talking to fred the farmer");

        return tasks;
    }
}
