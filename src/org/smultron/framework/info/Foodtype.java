package org.smultron.framework.info;

/**
 * Thanks to @iMEKlyi
 */
public enum Foodtype {
	SHRIMP("Shrimp", 3.0),
	COOKED_CHICKEN("Cooked chicken", 3.0),
	SARDINE("Sardine", 3.0),
	COOKED_MEAT("Cooked meat", 3.0),
	BREAD("Bread", 5.0),
	HERRING("Herring", 5.0),
	MACKEREL("Mackerel", 6.0),
	TROUT("Trout", 7.0),
	COD("Cod", 7.0),
	PIKE("Pike", 8.0),
	ROAST_BEAST_MEAT("Roast beast meat", 8.0),
	PINEAPPLE_PUNCH("Pineapple punch", 9.0),
	SALMON("Salmon", 9.0),
	TUNA("Tuna", 10.0),
	JUG_OF_WINE("Jug of wine", 11.0),
	RAINBOW_FISH("Rainbow fish", 11.0),
	STEW("Stew", 11.0),
	CAKE("Cake", 4.0),
	MEAT_PIE("Meat pie", 6.0),
	LOBSTER("Lobster", 12.0),
	BASS("Bass", 13.0),
	PLAIN_PIZZA("Plain pizza", 7.0),
	SWORDFISH("Swordfish", 14.0),
	POTATO_WITH_BUTTER("Potato with butter", 14.0),
	CHOCOLATE_CAKE("Chocolate cake", 5.0),
	TANGLED_TOADS_LEGS("Tangled toads legs", 15.0),
	POTATO_WITH_CHEESE("Potato with cheese", 16.0),
	MEAT_PIZZA("Meat pizza", 8.0),
	MONKFISH("Monkfish", 16.0),
	ANCHOVY_PIZZA("Anchovy pizza", 9.0),
	COOKED_KARAMBWAN("Cooked karambwan", 18.0),
	CURRY("Curry", 19.0),
	UGTHANKI_KEBAB("Ugthanki kebab", 19.0),
	MUSHROOM_POTATO("Mushroom potato", 20.0),
	SHARK("Shark", 20.0),
	SEA_TURTLE("Sea turtle", 21.0),
	PINEAPPLE_PIZZA("Pineapple pizza", 11.0),
	MANTA_RAY("Manta ray", 22.0),
	TUNA_POTATO("Tuna potato", 22.0),
	DARK_CRAB("Dark crab", 22.0),
	ANGLERFISH("Anglerfish", 22.0);

	private final String name;
	private final double heal;

	Foodtype(String name, double heal) {
		this.name = name;
		this.heal = heal;
	}

	public String getName() {
		return name;
	}

	public double getHeal() {
		return heal;
	}

}
