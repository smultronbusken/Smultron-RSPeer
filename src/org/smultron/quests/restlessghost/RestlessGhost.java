package org.smultron.quests.restlessghost;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.ItemTables;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import org.smultron.framework.Location;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.UseItemOn;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Quest;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.LeafNode;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.BinaryBranch;
import org.smultron.framework.thegreatforest.VarpBranch;


import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

/**
 * TODO: Make sure we are wearing the amulet does not work if the user pauses midquest and drops it
 * TODO; If the user pauses at the ghost parts until the ghost dissapears, it will get stuck.
 */
public class RestlessGhost extends TreeTask
{
    private int varpBit = Quest.THE_RESTLESS_GHOST.getVarpbit();
    private int questStages = Quest.THE_RESTLESS_GHOST.getStages();


    public RestlessGhost(TaskListener listener) {
	super(listener, "Completing The Restless Ghost");
    }

    @Override public TreeNode onCreateRoot() {
        BooleanSupplier isGhostLoaded = () -> Npcs.getLoaded(npc -> npc.getName().equals("Restless ghost")) != null;
        VarpBranch quest = new VarpBranch(varpBit);

        String[] dialogAereck = new String[] { "I'm looking for a quest!", "Ok, let me help then." };
        TreeNode talkAereck = new ProcessDialogTree(dialogAereck, () -> Npcs.getNearest("Father Aereck"));
        TreeNode atAereck = new InArea(talkAereck, CommonLocation.LUMBRIDGE_CHURCH, 3);
        quest.put(0, atAereck);

        String[] dialogUrhney = new String[] { "Father Aereck sent me to talk to you.", "He's got a ghost haunting his graveyard." };
        TreeNode talkUrhney = new ProcessDialogTree(dialogUrhney, () -> Npcs.getNearest("Father Urhney"));
        TreeNode atUrhney = new InArea(talkUrhney, CommonLocation.LUMBRIDGE_FATHER_URHNEY, 1);
        quest.put(1, atUrhney);

        TreeNode talkToGhost = new ProcessDialogTree("Yep, now tell me what the problem is.", () -> Npcs.getNearest("Restless ghost"));
        Task openCoffin = new InteractWith<>("Search", () -> SceneObjects.getNearest("Coffin"));
        TreeNode isCoffinOpen = BinaryBranchBuilder.getNewInstance()
                .successNode(talkToGhost)
                .setValidation(isGhostLoaded)
                .failureNode(openCoffin)
                .build();
        TreeNode atGhostGrave = new InArea(isCoffinOpen, CommonLocation.LUMBRIDGE_GHOST, 2);
        quest.put(2, hasGhostspeakAmulet(atGhostGrave));

        // Empty leaf node because the varp value will change as soon as we pick up the skull
        quest.put(3, hasSkull(new LeafNode()));

        Task putSkull = new UseItemOn<>(() -> Inventory.getFirst("Ghost's skull"), () -> SceneObjects.getNearest("Coffin"));
        TreeNode atGhostGraveAfterSkull = new InArea(putSkull, CommonLocation.LUMBRIDGE_GHOST, 2);
        quest.put(4, hasSkull(hasGhostspeakAmulet(atGhostGraveAfterSkull)));

        return quest;
    }

    @Override public boolean validate() {
	return Varps.get(varpBit) == questStages;
    }

    private TreeNode hasGhostspeakAmulet(TreeNode successNode) {
        BooleanSupplier inventoryContainsItem = () -> Inventory.contains(4250, 552) && ItemTables.lookup(ItemTables.EQUIPMENT).contains(4250, 552);
        TreeNode hasItems = BinaryBranchBuilder.getNewInstance()
                .successNode(successNode)
                .setValidation(inventoryContainsItem)
                .failureNode(new FunctionalTask(() -> Log.severe("I have note amulet"))) // TODO
                .build();
        return hasItems;
    }

    private TreeNode hasSkull(TreeNode successNode) {
        Location altarRoom = Location.location(Area.rectangular(3111, 9564, 3121, 9568), " the altar room");
        Task takeSkull = new InteractWith<>("Search", () -> SceneObjects.getNearest("Altar"));
        TreeNode atAltar = new InArea(takeSkull, altarRoom, 3);
        TreeNode hasSkull = BinaryBranchBuilder.getNewInstance()
                .successNode(successNode)
                .setValidation(() -> Inventory.contains("Skull"))
                .failureNode(atAltar)
                .build();
        return hasSkull;
    }
}
