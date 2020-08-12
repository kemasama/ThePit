package net.devras.pit.common;

public class Tier {
	private TierType type;
	private int level;
	
	public Tier(int level, TierType type) {
		this.type = type;
		this.level = level;
	}
	
	public int toLevel(int amount) {
		int total = 0;
		
		if (level > 0 && amount > 10) {
			double per = level / amount;
			total = (int) Math.floor(per * 10);
		}
		
		
		return total;
	}

	public TierType getType() {
		return type;
	}
	public void setType(TierType type) {
		this.type = type;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
