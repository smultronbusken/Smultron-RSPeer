package org.smultron.quests.cooksassistant;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.ui.Log;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.content.item.gathering.GatherBucketOfMilk;
import org.smultron.framework.content.item.gathering.GatherPotOfFlour;
import org.smultron.framework.content.item.gathering.GatheringTasks;
import org.smultron.framework.content.item.gathering.GatheringTasks.GatherEgg;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.*;

import java.util.function.BooleanSupplier;

/**
 * Completes the quest CooksAssistant.
 * Assumes ~6 free inventory slots
 */
public class CooksAssistant extends TreeTask {
	public CooksAssistant(final TaskListener listener) {
		super(listener, "Completing Cooks Assistant.");
	}

	@Override
	public TreeNode onCreateRoot() {
		VarpBranch quest = new VarpBranch(Quest.COOKS_ASSISTANT.getVarpbit());

        /*
        Start the quest
         */
		String[] startQuestDialog = new String[]{
				"What's wrong?",
				"I'm always happy to help a cook in distress."
		};
		TreeNode speak = new ProcessDialogTree(() -> Npcs.getNearest("Cook"), startQuestDialog);
		TreeNode atCooksKitchen = new InArea(speak, CommonLocation.LUMBRIDGE_COOK, 3);
		quest.put(0, atCooksKitchen);

        /*
        Gather all ingredients, then speak with the Cook
         */

		Task gatherItems = new ArrayTask(null, "Gathering the ingredients.")
		{
		    @Override protected Task[] createTasks() {
			Task gatherEgg = new GatherEgg(this);
			Task gatherMilk = new GatherBucketOfMilk(this);
			Task gatherFlour = new GatherPotOfFlour(this);
		        Task[] tasks = new Task[]{
				gatherEgg,
				gatherMilk,
				gatherFlour
			};
			return tasks;
		    }
		};


		Task talkTo = new TalkToNpc(() -> Npcs.getNearest("Cook")).asTask(null, "");
		TreeNode atChef = new InArea(talkTo, CommonLocation.LUMBRIDGE_COOK, 3);

		Task getItemsAndReturn = new ArrayTask(null, "") {
			@Override
			protected Task[] createTasks() {
				gatherItems.attachListener(this);
				Task[] tasks = new Task[]{
					gatherItems, atChef.asTask(this, "Giving the chef the ingredients.")
				};
				return tasks;
			}
		};

		quest.put(1, getItemsAndReturn);
		return quest;
	}

	@Override
	public boolean validate() {
		return Varps.get(Quest.COOKS_ASSISTANT.getVarpbit()) == Quest.COOKS_ASSISTANT.getStages();
	}
}
