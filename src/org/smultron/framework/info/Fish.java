package org.smultron.framework.info;

import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;

/**
 * Thanks to @Roma
 */
public enum Fish
{
	SHRIMP("Small fishing net", "Fishing spot", "Net", "", 1),
	SARDINE("Sardine", "Raw sardine", "Fishing rod", "Bait", 5),
	KARAMBWANJI("Karambwanji", "Raw karambwanji", "Net", "", 5),
	HERRING("Herring", "Raw herring", "Fishing rod", "Bait", 10),
	ANCHOVIES("Anchovies", "Raw anchovies", "Net", "", 15),
	MACKEREL("Mackerel", "Raw mackerel", "Big net", "", 16),
	TROUT("Fly fishing rod", "Rod fishing spot", "Lure", "Feather", 20),
	COD("Cod", "Raw cod", "Big net", "Bait", 23),
	PIKE("Pike", "Raw pike", "Fishing rod", "Bait", 25),
	SLIMY_EEL("Slimy eel", "Slimy eel", "Fishing rod", "Bait", 28),
	SALMON("Salmon", "Raw salmon", "Fly fishing rod", "Feather", 30),
	FROG_SPAWN("Frog spawn", "Frog spawn", "Net", "", 33),
	TUNA("Tuna", "Raw tuna", "Harpoon", "", 35),
	RAW_RAINBOW_FISH("Rainbow fish", "Raw rainbow fish", "Fly fishing rod", "", 38),
	CAVE_EEL("Cave eel", "Raw cave eel", "Fishing rod", "Bait", 38),
	LOBSTER("Lobster", "Raw lobster", "Lobster cage", "", 40),
	BASS("Bass", "Raw bass", "Big net", "", 46),
	SWORDFISH("Swordfish", "Raw swordfish", "Harpoon", "", 50),
	MONKFISH("Monk fish", "Raw monkfish", "Net", "", 62),
	KARAMBWAN("Karambwan", "Raw karambwan", "Karambwan vessel", "", 65),
	SHARK("Shark", "Raw shark", "Harpoon", "", 76);

	@Override
	public String toString() {
	    return name().substring(0,1).toUpperCase() + name().substring(1).toLowerCase().replaceAll("_", " ");
	}

	private final String equipment, spot, action, bait;
	private final int requiredLevel;

	Fish(final String equiptment, final String spot, final String action, final String bait, final int requiredLevel) {
		this.equipment = equiptment;
		this.spot = spot;
		this.requiredLevel = requiredLevel;
		this.action = action;
		this.bait = bait;
	}

	public String getEquiptment() { return equipment; }

	public String getSpot() { return spot; }

	public String getAction() { return action; }

	public String getBait() { return bait; }

	public int getRequiredLevel() { return requiredLevel; }

	public boolean isAccessible() {
		return Skills.getCurrentLevel(Skill.FISHING) >= requiredLevel;
	}
}
