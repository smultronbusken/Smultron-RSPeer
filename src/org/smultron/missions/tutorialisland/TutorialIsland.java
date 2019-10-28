package org.smultron.missions.tutorialisland;

import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell.Modern;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.ui.Log;
import org.smultron.framework.Location;
import org.smultron.framework.content.Equip;
import org.smultron.framework.tasks.FunctionalTask;
import org.smultron.framework.content.InteractWith;
import org.smultron.framework.content.MoveTo;
import org.smultron.framework.tasks.ArrayTask;
import org.smultron.framework.tasks.Task;
import org.smultron.framework.tasks.TaskListener;
import org.smultron.framework.content.UseItemOn;
import org.smultron.framework.thegreatforest.InArea;
import org.smultron.framework.thegreatforest.BinaryBranchBuilder;
import org.smultron.framework.thegreatforest.VarpBranch;
import org.smultron.framework.content.dialog.ProcessDialogTree;
import org.smultron.framework.content.dialog.TalkToNpc;
import org.smultron.framework.thegreatforest.LeafNode;
import org.smultron.framework.thegreatforest.TreeNode;
import org.smultron.framework.thegreatforest.TreeTask;
import org.smultron.framework.info.Fish;
import org.smultron.framework.info.KnownVarp;


import java.util.function.Predicate;
import java.util.function.Supplier;

public class TutorialIsland extends TreeTask
{
    private static final int VARP = KnownVarp.TUTORIAL_ISLAND.getId();;
    private String accountName;

    /*
    Areas
     */
    private static final Location MASTER_CHEF_AREA = Location.location(3073, 3083, 3078, 3086, "Master Chef");
    private static final Location QUEST_GUIDE_AREA = Location.location(3083, 3119, 3088, 3125, "Quest Guide");
    private static final Location MINING_INSTRUCTOR_AREA = Location.location(3080, 9502, 3086, 9508, "Mining Instructor");
    private static final Location COMBAT_INSTRUCTOR_AREA = Location.location(Area.polygonal(new Position(3104, 9504, 0),
								      new Position(3096, 9501, 0),
								      new Position(3096, 9504, 0),
								      new Position(3100, 9505, 0),
								      new Position(3109, 9513, 0),
								      new Position(3113, 9513, 0)),
									    "Combat instructor");
    private static final Location RAT_AREA = Location.location(3108, 9517, 3109, 9520);
    private static final Location BANK_AREA = Location.location(3118, 3119, 3123, 3125);
    private static final Location BROTHER_BRACE_AREA = Location.location(3120, 3110, 3128, 3103, "Brother Brace");
    private static final Location ACCOUNT_GUIDE_AREA = Location.location(3125, 3123, 3129, 3125, "Account Guide");
    private static final Location MAGIC_INSTRUCTOR_AREA = Location.location(3139, 3088, 3142, 3090, "Magic Instructor");

    /*
    Suppliers for npcs
     */
    private Supplier<Npc> gielinorGuide = () -> Npcs.getNearest("Gielinor Guide");
    private Supplier<Npc> survivalExpert = () -> Npcs.getNearest("Survival Expert");
    private Supplier<Npc> masterChef = () -> Npcs.getNearest("Master Chef");
    private Supplier<Npc> questGuide = () -> Npcs.getNearest("Quest Guide");
    private Supplier<Npc> miningInstructor = () -> Npcs.getNearest("Mining Instructor");
    private Supplier<Npc> combatInstructor = () -> Npcs.getNearest("Combat Instructor");
    private Supplier<Npc> accountGuide = () -> Npcs.getNearest("Account Guide");
    private Supplier<Npc> brotherBrace = () -> Npcs.getNearest("Brother Brace");
    private Supplier<Npc> magicInstructor = () -> Npcs.getNearest("Magic Instructor");

    /*
    AreaBranches for npcs i.e "Make sure we are in the correct area THEN talk"-tasks
     */
    private TreeNode talkToMasterChef = new InArea(new TalkToNpc(masterChef), MASTER_CHEF_AREA, 2);
    private TreeNode talkToQuestGuide = new InArea(new TalkToNpc(questGuide), QUEST_GUIDE_AREA, 2);
    private TreeNode talkToMiningInstructor = new InArea(new TalkToNpc(miningInstructor), MINING_INSTRUCTOR_AREA, 2);
    private TreeNode talkToCombatInstructor = new InArea(new TalkToNpc(combatInstructor), COMBAT_INSTRUCTOR_AREA, 2);
    private TreeNode talkToBrotherBrace = new InArea(new TalkToNpc(brotherBrace), BROTHER_BRACE_AREA, 2);
    private TreeNode talkToMagicInstructor = new InArea(new TalkToNpc(magicInstructor), MAGIC_INSTRUCTOR_AREA, 2);
    private TreeNode talkToAccountGuide = new InArea(new TalkToNpc(accountGuide), ACCOUNT_GUIDE_AREA, 2);

    public TutorialIsland(final TaskListener listener, String accountName) {
	super(listener, "Completing tutorial island.");
	this.accountName = accountName;
    }

    @Override public TreeNode onCreateRoot() {
	VarpBranch quest = new VarpBranch(VARP);
	Predicate<Npc> rat = npc -> npc.getTarget() == null && npc.getName().equals("Giant rat");
	Task attackRat = new InteractWith<>("Attack", Npcs::getNearest, rat);

	quest.put(1, new ConfigureCharacter(accountName));

	/*
	  Gielinor Guide
	 */
	quest.put(2, new ProcessDialogTree("I am an experienced player.", gielinorGuide));
	quest.put(3, new LeafNode(openTab(Tab.OPTIONS)));
	quest.put(7, new TalkToNpc(gielinorGuide));
	quest.put(10, new InteractWith<SceneObject>("Open", () -> SceneObjects.getNearest("Door")));

	/*
	  Survival Expert
	 */
	quest.put(20, new TalkToNpc(survivalExpert));
	quest.put(30, new LeafNode(openTab(Tab.INVENTORY)));



	quest.put(40, new InteractWith<>(Fish.SHRIMP.getAction(), () -> Npcs.getNearest(Fish.SHRIMP.getSpot())));
	quest.put(50, new LeafNode(openTab(Tab.SKILLS)));
	quest.put(60, new TalkToNpc(survivalExpert));
	quest.put(70, new InteractWith<>("Chop down", () -> SceneObjects.getNearest("Tree")));
	quest.put(80, new LightFire(null, item -> item.getName().equals("Logs")));
	quest.put(90, new UseItemOn<>(() -> Inventory.getFirst("Raw shrimps"), () -> SceneObjects.getNearest("Fire")));
	quest.put(120,new MoveTo(MASTER_CHEF_AREA, 1));

	/*
	  Master Chef
	 */
	quest.put(130, new MoveTo(MASTER_CHEF_AREA, 1));
	quest.put(140, talkToMasterChef);
	quest.put(150, new UseItemOn<>(() -> Inventory.getFirst("Pot of flour"), () -> Inventory.getFirst("Bucket of water")));
	quest.put(160, new InteractWith<>("Cook", () -> SceneObjects.getNearest("Range")));
	quest.put(170, new MoveTo(QUEST_GUIDE_AREA, 3));

	/*
	  Walk to Quest guide
	 */
	quest.put(200, new MoveTo(QUEST_GUIDE_AREA, 3));
	quest.put(220, talkToQuestGuide);
	quest.put(230, openTab(Tab.QUEST_LIST));
	quest.put(240, talkToQuestGuide);

	/*
	  Mining instructor
	 */
	quest.put(250, new MoveTo(MINING_INSTRUCTOR_AREA, 2));
	quest.put(260, talkToMiningInstructor);
	quest.put(270, new InteractWith<>("Prospect", () -> SceneObjects.getNearest("Rocks")));
	quest.put(300, new InArea(new InteractWith<>("Mine", () -> SceneObjects.getNearest("Rocks")), Location.getHintArrow(), 2));
	quest.put(310, new InArea(new InteractWith<>("Mine", () -> SceneObjects.getNearest("Rocks")), Location.getHintArrow(), 2));
	quest.put(320, new InteractWith<>("Use", () -> SceneObjects.getNearest("Furnace")));
	quest.put(330, talkToMiningInstructor);
	quest.put(340, new InteractWith<>("Smith", () -> SceneObjects.getNearest("Anvil")));
	quest.put(350, new FunctionalTask(() -> {
	    InterfaceComponent bronzeDagger = Interfaces.getComponent(312, 9);
	    if(bronzeDagger != null)
		bronzeDagger.click();
	}));

	/*
	  Combat Instructor
	 */
	quest.put(360, new MoveTo(COMBAT_INSTRUCTOR_AREA, 2));
	quest.put(370, talkToCombatInstructor);
	quest.put(390, openTab(Tab.EQUIPMENT));
	quest.put(400, new FunctionalTask(() -> {
	    InterfaceComponent wornEquipment = Interfaces.getComponent(387, 17);
	    if(wornEquipment != null)
		wornEquipment.click();
	}));
	quest.put(405, new InteractWith<>("Wield", () -> Inventory.getFirst("Bronze dagger")));
	quest.put(410, talkToCombatInstructor);

	/*
	  Melee
	 */
	quest.put(420, new Equip(null, new String[]{"Bronze sword", "Wooden shield"}, Equip.EquipType.WIELD));
	quest.put(430, openTab(Tab.COMBAT));
	quest.put(440, new MoveTo(RAT_AREA, 4));
	quest.put(450, attackRat);
	quest.put(460, attackRat);
	quest.put(470, talkToCombatInstructor);

	/*
	  Archery
	 */
	Task equipBowAndAttack = new ArrayTask(null, "Attacking the rat with my bow.")
	{
	    @Override protected Task[] createTasks() {
		String[] items = new String[]{"Bronze arrow", "Shortbow"};
		Task[] tasks = new Task[]{
			new Equip(this, items, Equip.EquipType.WIELD),
			attackRat
		};
	        return tasks;
	    }
	};
	quest.put(480, equipBowAndAttack);
	quest.put(490, attackRat);

	/*
	  Bank + Poll booth
	 */
	quest.put(500, new MoveTo(BANK_AREA, 2));
	quest.put(510, new InteractWith<>("Use", () -> SceneObjects.getNearest("Bank booth")));
	TreeNode useThePollBooth = BinaryBranchBuilder.getNewInstance()
		.successNode(new FunctionalTask(Dialog::processContinue))
		.setValidation(Dialog::canContinue)
		.failureNode(new InteractWith<>("Use", () -> SceneObjects.getNearest("Poll booth")))
		.build();
	quest.put(520, useThePollBooth);

	/*
	  Account Guide
	 */
	quest.put(530, talkToAccountGuide);
	quest.put(531, openTab(Tab.ACCOUNT_MANAGEMENT));
	quest.put(532, talkToAccountGuide);

	/*
	  Brother Brace
	 */
	quest.put(540, new MoveTo(null, BROTHER_BRACE_AREA, 1));
	quest.put(550, talkToBrotherBrace);
	quest.put(560, openTab(Tab.PRAYER));
	quest.put(570, talkToBrotherBrace);
	quest.put(580, openTab(Tab.FRIENDS_LIST));
	quest.put(600, talkToBrotherBrace);

	/*
	  Magic instructor
	 */
	quest.put(610, new MoveTo(MAGIC_INSTRUCTOR_AREA, 0 ));
	quest.put(620, talkToMagicInstructor);
	quest.put(630, openTab(Tab.MAGIC));
	quest.put(640, talkToMagicInstructor);

	Task castSpell = new FunctionalTask(() -> {
	    Predicate<Npc> chicken = npc -> npc.getName().equals("Chicken") &&
					    npc.getTarget() == null;
	    Interactable target = Npcs.getNearest(chicken);
	    if (target != null)
		Magic.cast(Modern.WIND_STRIKE, target);
	    else
		Log.severe("Chicken cannot be found!");
	});
	quest.put(650, castSpell);

	String[] magicInstructorDialog = new String[]{
		"Yes.",
		"No, I'm not planning to do that."
	};
	TreeNode nearMagicInstructor = new InArea(new ProcessDialogTree(magicInstructorDialog, magicInstructor), MAGIC_INSTRUCTOR_AREA, 2);
	quest.put(670, nearMagicInstructor);

	return quest;
    }

    @Override public boolean validate() {
	return Varps.get(VARP) == 1000;
    }

    private Task openTab(Tab tab) {
        return new FunctionalTask(() ->
	{
	    // This seems to help in rare cases
	    if (Dialog.canContinue()){
		Dialog.processContinue();
		Time.sleep(600, 1200);
	    }
	    tab.getComponent().interact(tab.toString());
	}
	).setName("Opening " + tab + " tab");
    }
}
