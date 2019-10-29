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
import org.smultron.framework.info.Quest;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.VarpBranch;

import java.util.function.BooleanSupplier;

/**
 * Completes the quest CooksAssistant.
 * Assumes ~6 free inventory slots
 */
public class CooksAssistant extends TreeTask
{
    private final int varp = Quest.COOKS_ASSISTANT.getVarpbit();

    public CooksAssistant(final TaskListener listener)
    {
        super(listener, "Completing Cooks Assistant.");
    }

    @Override
    public TreeNode onCreateRoot() {
        VarpBranch quest = new VarpBranch(varp);

        /*
        Start the quest
         */
        String[] startQuestDialog = new String[]{
                "What's wrong?",
                "I'm always happy to help a cook in distress."
        };
        TreeNode speak = new ProcessDialogTree(startQuestDialog, () -> Npcs.getNearest("Cook"));
        TreeNode atCooksKitchen = new InArea(speak, CommonLocation.LUMBRIDGE_COOK, 3);
        quest.put(0, atCooksKitchen);

        /*
        Gather all ingredients, then speak with the Cook
         */
        TreeNode giveCookAllItems = new TalkToNpc(() -> Npcs.getNearest("Cook"));
        TreeNode atCooksKitchen2 = new InArea(giveCookAllItems, CommonLocation.LUMBRIDGE_COOK, 3);

        Task gatherEgg = new GatheringTasks.GatherEgg(null);
        Task gatherMilk = new GatherBucketOfMilk(null);
        Task gatherFlour = new GatherPotOfFlour(null);

        BooleanSupplier hasEgg = () -> Inventory.contains("Egg");
        BooleanSupplier hasMilk = () -> Inventory.contains("Bucket of milk");
        BooleanSupplier hasFlour = () -> Inventory.contains("Pot of flour");

        TreeNode shouldGatherFlour = BinaryBranchBuilder.getNewInstance()
                .successNode(new FunctionalTask(() -> Log.fine("I have gathered all the items!")))
                .setValidation(hasFlour)
                .failureNode(gatherFlour)
                .build();
        TreeNode shouldGatherMilk = BinaryBranchBuilder.getNewInstance()
                .successNode(shouldGatherFlour)
                .setValidation(hasMilk)
                .failureNode(gatherMilk)
                .build();
        TreeNode shouldGatherEgg = BinaryBranchBuilder.getNewInstance()
                .successNode(shouldGatherMilk)
                .setValidation(hasEgg)
                .failureNode(gatherEgg)
                .build();
        TreeNode hasAllItems = BinaryBranchBuilder.getNewInstance()
                .successNode(atCooksKitchen2)
                .setValidation(() -> hasEgg.getAsBoolean() && hasMilk.getAsBoolean() && hasFlour.getAsBoolean())
                .failureNode(shouldGatherEgg)
                .build();


        Task talkTo = new TalkToNpc(() -> Npcs.getNearest("Cook")).asTask(null, "");
        Task getItemsAndReturn = new ArrayTask(null, "")
        {
            @Override protected Task[] createTasks() {
                Task moveTo = new MoveTo(this, CommonLocation.LUMBRIDGE_COOK, 3);
                Task[] tasks = new Task[] {
                        hasAllItems.asTask(this, "Getting all items"),
                        moveTo,
                        talkTo
                };
                return tasks;
            }
        };

        quest.put(1, getItemsAndReturn);
        return quest;
    }

    @Override public boolean validate() {
        return Varps.get(varp) == Quest.COOKS_ASSISTANT.getStages();
    }
}
