package net.devras.pit.common;

public enum Perk {
	NONE(0),

	// NORMAL
	GOLDEN_HEAD(10000),
	FISHING_ROD(10000),
	ENDLESS(10000),
	SAFETY_FIRST(5000),
	STRENGTH(20000),
	VAMPIRE(40000),

	// EXTRA
	DIRTY(6000),
	Kung_FU(10000);

	private Perk(int gold) {
		this.gold = gold;
	}

	private int gold;
	public int getGold() {
		return gold;
	}

	public static Perk getPark(String name, Perk def) {
		Perk park = def;
		for (Perk park2: Perk.values()) {
			if (park2.name().equals(name)) {
				park = park2;
			}
		}
		return park;
	}
}
