package org.smultron.scripts.halloween;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.ScriptMeta;
import org.smultron.framework.Location;
import org.smultron.framework.MullbarScript;
import org.smultron.framework.content.Equip;
import org.smultron.framework.content.Equip.EquipType;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.UseItemOn;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.info.CommonLocation;
import org.smultron.framework.info.Store;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.VarpBranch;

import java.util.function.Supplier;

@ScriptMeta(name = "Halloween 2019", developer = "Smultron", desc = "Completes the halloween 2019 event", version = 1.1)
public class Hallowen extends MullbarScript {
	private static final Supplier<Npc> SNAILNECK = () -> Npcs.getNearest("Snailneck");
	private static final Supplier<Npc> EPIC_MAGER = () -> Npcs.getNearest("epic mager34");
	private static final Location START_AREA = Location.location(Area.rectangular(3235, 3281, 3241, 3288));
	private static final Location BOBS_SHOP = Location.location(Area.rectangular(3227, 3201, 3233, 3205));
	private static final Task CONTINUE_DIALOG = new FunctionalTask(Dialog::processContinue);
	private static final TreeNode TALK_WITH_SNAILNECK = new InArea(new TalkToNpc(SNAILNECK), START_AREA, 1);
	private static final TreeNode TALK_WITH_EPIC_MAGER = new InArea(new TalkToNpc(EPIC_MAGER), START_AREA, 1);

	@Override
	public Task nextTask() {
		VarpBranch quest = new VarpBranch(2603);
		quest.put(0, TALK_WITH_SNAILNECK);
		quest.put(40960, new InArea(new ProcessDialogTree(EPIC_MAGER, "Yes, of course!"), START_AREA, 1));
		TreeNode talkWithBob = new ProcessDialogTree(() -> Npcs.getNearest("Bob"), "About Halloween.");
		TreeNode atAreaBob = new InArea(talkWithBob, BOBS_SHOP, 1);
		quest.put(81920, atAreaBob);
		quest.put(122880, CONTINUE_DIALOG);
		TreeNode talkWithFather = new ProcessDialogTree(() -> Npcs.getNearest("Father Aereck"), "About Halloween.");
		TreeNode atChurch = new InArea(talkWithFather, CommonLocation.LUMBRIDGE_CHURCH, 3);
		quest.put(122882, atChurch);
		quest.put(164866, TALK_WITH_EPIC_MAGER);
		quest.put(205826, TALK_WITH_SNAILNECK);
		quest.put(246786, TALK_WITH_EPIC_MAGER);
		quest.put(254978, gatherSheetsSection());

		/*
		Make and wear the costume
		 */
		Supplier<Item> knife = () -> Inventory.getFirst(item -> item.getName().equals("Knife"));
		Supplier<Item> sheets = () -> Inventory.getFirst(item -> item.getName().equals("White bed sheets"));
		Task make = new UseItemOn<>(knife, sheets);
		quest.put(287746, make);
		String[] items = new String[]{"Spooky gloves", "Spooky boots", "Spooky skirt", "Spooky robe", "Spooky hood"};
		Task wear = new Equip(this, items, EquipType.WEAR);
		TreeNode equipOrTalk = BinaryBranchBuilder.getNewInstance()
				.successNode(TALK_WITH_EPIC_MAGER)
				.setValidation(wear::validate) // The Equip task only validates when all items are equiped
				.failureNode(wear)
				.build();
		quest.put(295938, new InArea(equipOrTalk, START_AREA, 1));

		/*
		Answer the riddles
		 */
		quest.put(328706, TALK_WITH_SNAILNECK);
		String[] answers = new String[]{
				"Goblin mail.",
				"Fight about it.",
				"Put them in prison."
		};
		TreeNode answerQuestions = new InArea(new ProcessDialogTree(SNAILNECK, answers), START_AREA, 1);

		quest.put(369666, answerQuestions);
		quest.put(377858, answerQuestions);
		quest.put(386050, answerQuestions);
		quest.put(410626, TALK_WITH_SNAILNECK);
		quest.put(451586, TALK_WITH_SNAILNECK);
		quest.put(459778, TALK_WITH_SNAILNECK);
		quest.put(492546, takeAndIgnitePowder());
		quest.put(2589698, stealShinyGlass());

		/*
		Finish up
		 */
		quest.put(533506, TALK_WITH_SNAILNECK);
		quest.put(615426, CONTINUE_DIALOG);
		quest.put(656386, CONTINUE_DIALOG);
		quest.put(697346, new FunctionalTask(() -> setStopping(true)));
		return quest.asTask(this, "Completing Halloween 2019 event");
	}

	private TreeNode gatherSheetsSection() {
		Task takeSheet = new InteractWith<>("Take-sheets", () -> SceneObjects.getNearest("Bed"));
		Area house = Area.rectangular(3234, 3205, 3236, 3208);
		TreeNode atSheetLocation = new InArea(takeSheet, Location.location(house), 0);

		Area knifeLocation = Area.rectangular(3222, 3200, 3228, 3207);
		Task pickUpKnife = new InteractWith<>("Take", () -> Pickables.getNearest("Knife"));
		TreeNode atKnifeLocation = new InArea(pickUpKnife, Location.location(knifeLocation), 1);

		TreeNode hasKnife = BinaryBranchBuilder.getNewInstance()
				.successNode(TALK_WITH_EPIC_MAGER)
				.setValidation(() -> Inventory.contains("Knife"))
				.failureNode(atKnifeLocation)
				.build();
		TreeNode hasSheets = BinaryBranchBuilder.getNewInstance()
				.successNode(hasKnife)
				.setValidation(() -> Inventory.contains("White bed sheets"))
				.failureNode(atSheetLocation)
				.build();
		TreeNode hasSheetsAndKnife = BinaryBranchBuilder.getNewInstance()
				.successNode(TALK_WITH_EPIC_MAGER)
				.setValidation(() -> Inventory.contains("White bed sheets") && Inventory.contains("Knife"))
				.failureNode(hasSheets)
				.build();
		return hasSheetsAndKnife;
	}

	private TreeNode takeAndIgnitePowder() {
		Area furnace = Area.rectangular(3225, 3249, 3230, 3255);

		Task blackPowder = new InteractWith<>("Grab-powder", () -> SceneObjects.getNearest("Black powder"));
		TreeNode atFurnace = new InArea(blackPowder, Location.location(furnace), 2);

		Task dropPowder = new InteractWith<>("Ignite", () -> Inventory.getFirst("Smoke powder"));
		TreeNode atGeneralStore = new InArea(dropPowder, Store.GENERAL_STORE_LUMBRIDGE, 1);

		TreeNode hasPowder = BinaryBranchBuilder.getNewInstance()
				.successNode(atGeneralStore)
				.setValidation(() -> Inventory.contains("Smoke powder") || Store.GENERAL_STORE_LUMBRIDGE.asArea().contains(Players.getLocal()))
				.failureNode(atFurnace)
				.build();
		TreeNode hasShinyGlass = BinaryBranchBuilder.getNewInstance()
				.successNode(TALK_WITH_SNAILNECK)
				.setValidation(() -> Inventory.contains("Shiny glass"))
				.failureNode(hasPowder)
				.build();
		return hasShinyGlass;
	}

	private TreeNode stealShinyGlass() {
		Task stealGlass = new InteractWith<>("Steal", () -> Pickables.getNearest("Shiny glass"));
		TreeNode canStealGlass = BinaryBranchBuilder.getNewInstance()
				.successNode(stealGlass)
				.setValidation(Dialog::canContinue)
				.failureNode(CONTINUE_DIALOG)
				.build();
		TreeNode didSteal = BinaryBranchBuilder.getNewInstance()
				.successNode(TALK_WITH_SNAILNECK)
				.setValidation(() -> Inventory.contains("Shiny glass"))
				.failureNode(canStealGlass)
				.build();
		return didSteal;
	}
}
