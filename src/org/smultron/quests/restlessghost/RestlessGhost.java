package org.smultron.quests.restlessghost;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.smultron.framework.Task;
import org.smultron.framework.TaskListener;
import org.smultron.framework.tasks.dialog.DoDialogTree;
import org.smultron.framework.tasks.atoms.InteractWithNearestObject;
import org.smultron.framework.tasks.atoms.UseItemOnSceneObject;
import org.smultron.framework.tasks.inventory.WearEquipment;
import org.smultron.framework.tasks.movement.MoveTo;
import org.smultron.framework.thegreatforest.LeafNode;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.thegreatforest.BinaryBranch;
import org.smultron.framework.thegreatforest.QuestBranch;
import org.smultron.info.FreeQuest;
import org.smultron.info.Location;
import org.smultron.info.SceneobjectAction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;

/**
 * TODO: Make sure we are wearing the amulet does not work if the user pauses midquest and drops it
 * TODO; If the user pauses at the ghost parts until the ghost dissapears, it will get stuck.
 */
public class RestlessGhost extends TreeTask
{
    private int varpBit = FreeQuest.THE_RESTLESS_GHOST.getVarpbit();
    private int questStages = FreeQuest.THE_RESTLESS_GHOST.getStages();


    public RestlessGhost(TaskListener listener) {
	super(listener, "Completing The Restless Ghost");
    }

    @Override public TreeNode onCreateRoot() {
        /*
        Start quest
         */
        Task moveToChurch = new MoveTo(Location.LUMBRIDGE_CHURCH, 6);
        Deque<String> dialogAereck = new ArrayDeque<>();
        dialogAereck.addLast("I'm looking for a quest!");
        dialogAereck.addLast("Ok, let me help then.");
        TreeNode talkAereck = new DoDialogTree(dialogAereck, "Father Aereck");
        TreeNode startQuest = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(moveToChurch);
            }

            @Override public TreeNode successNode() {
                return talkAereck;
            }

            @Override public boolean validate() {
                return Location.LUMBRIDGE_CHURCH.getArea().contains(Players.getLocal());
            }
        };

        /*
        Get the amulet
         */
        Task moveToFatherUrhney = new MoveTo(Location.LUMBRIDGE_FATHER_URHNEY, 2);
        Deque<String> dialogUrhney = new ArrayDeque<>();
        dialogUrhney.addLast("Father Aereck sent me to talk to you.");
        dialogUrhney.addLast("He's got a ghost haunting his graveyard.");
        TreeNode talkUrhney = new DoDialogTree(dialogUrhney, "Father Urhney");
        TreeNode getAmulet = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(moveToFatherUrhney);
            }

            @Override public TreeNode successNode() {
                return talkUrhney;
            }

            @Override public boolean validate() {
                return Location.LUMBRIDGE_FATHER_URHNEY.getArea().contains(Players.getLocal());
            }
        };

        /*
        Talk to the ghost
         */
        Task moveToGhost = new MoveTo(Location.LUMBRIDGE_GHOST, 3);
        Task openCoffin = new InteractWithNearestObject("Coffin", SceneobjectAction.SEARCH);
        Task equipAmulet = new WearEquipment(null, new ArrayList<>(Collections.singletonList("Ghostspeak amulet")));
        Deque<String> dialogGhost = new ArrayDeque<>();
        dialogGhost.addLast("Yep, now tell me what the problem is.");
        TreeNode talkGhost = new DoDialogTree(dialogGhost, "Restless ghost");
        TreeNode hasAmulet = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(equipAmulet);
            }

            @Override public TreeNode successNode() {
                return talkGhost;
            }

            @Override public boolean validate() {
                return Equipment.contains("Ghostspeak amulet");
            }
        };
        TreeNode shouldOpenCoffin = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(openCoffin);
            }

            @Override public TreeNode successNode() {
                return hasAmulet;
            }

            @Override public boolean validate() {

                return Npcs.getLoaded(npc -> npc.getName().equals("Restless ghost")) != null;
            }
        };
        TreeNode isAtGhost = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(moveToGhost);
            }

            @Override public TreeNode successNode() {
                return shouldOpenCoffin;
            }

            @Override public boolean validate() {
                return Location.LUMBRIDGE_GHOST.getArea().contains(Players.getLocal());
            }
        };

        /*
        Take the skull
         */
        Area altarRoom = Area.rectangular(3111, 9564, 3121, 9568);
        Task moveToRoom = new MoveTo(altarRoom, 1);
        Task takeSkull = new InteractWithNearestObject("Altar", SceneobjectAction.SEARCH);
        TreeNode atAltar = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(moveToRoom);
            }
            @Override public TreeNode successNode() {
                return new LeafNode(takeSkull);
            }
            @Override public boolean validate() {
                return altarRoom.contains(Players.getLocal());
            }
        };

        /*
        Use the skull
         */
        Task putSkull = new UseItemOnSceneObject("Ghost's skull", "Coffin", null);
        TreeNode canPutSkull = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(new InteractWithNearestObject("Coffin", SceneobjectAction.OPEN));
            }

            @Override public TreeNode successNode() {
                Time.sleep(2000);
                return new LeafNode(putSkull);
            }

            @Override public boolean validate() {
                SceneObject coffin = SceneObjects.getNearest("Coffin");
                return Arrays.asList(coffin.getActions()).contains("Close");
            }
        };
        TreeNode isAtCoffin = new BinaryBranch()
        {
            @Override public TreeNode failureNode() {
                return new LeafNode(moveToGhost);
            }

            @Override public TreeNode successNode() {
                return canPutSkull;
            }

            @Override public boolean validate() {
                return Location.LUMBRIDGE_GHOST.getArea().contains(Players.getLocal());
            }
        };

        HashMap<Integer, TreeNode> questSections = new HashMap<>();
        questSections.put(0, startQuest);
        questSections.put(1, getAmulet);
        questSections.put(2, isAtGhost);
        questSections.put(3, atAltar);
        questSections.put(4, isAtCoffin);
        TreeNode varpBranch = new QuestBranch(FreeQuest.THE_RESTLESS_GHOST.getVarpbit(), questSections);

        return varpBranch;
    }

    @Override public boolean validate() {
	return Varps.get(varpBit) == questStages;
    }
}
