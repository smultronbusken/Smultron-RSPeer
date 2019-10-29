package org.smultron.framework.info;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.smultron.framework.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

@SuppressWarnings("JavaDoc")
public enum Store implements Location {
	GENERAL_STORE_VARROCK(npc -> npc.getName().equals("Shop keeper"),
			Area.rectangular(3214, 3411, 3220, 3418),
			"Varrock General Store"),

	GENERAL_STORE_LUMBRIDGE(npc -> npc.getName().equals("Shop keeper") || npc.getName().equals("Shop assistant"),
			Area.rectangular(3208, 3244, 3214, 3249),
			"Lumbridge General Store"),

	WYDINS_FOOD_STORE(npc -> npc.getName().equals("Wydin"),
			Area.rectangular(3012, 3203, 3016, 3210),
			"Wydin's food store");

	private final Predicate<? super Npc> npc;
	private final Area area;
	private final String name;

	private Store(final Predicate<? super Npc> npc, final Area area, final String name) {
		this.npc = npc;
		this.area = area;
		this.name = name;
	}

	public Predicate<? super Npc> getNpc() {
		return npc;
	}

	@Override
	public Position asPosition() {
		return area.getCenter();
	}

	@Override
	public Area asArea() {
		return area;
	}

	@Override
	public String locationName() {
		return name;
	}

	// TODO Comperator for Store
	public static final ArrayList<Store> GENERAL_STORES = new ArrayList<>(Arrays.asList(
			GENERAL_STORE_LUMBRIDGE,
			GENERAL_STORE_VARROCK
	));

}
