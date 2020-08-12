package net.devras.pit.common;

public enum Status {
	IDLE("§aIdling"),
	FIGHT("§cFighting"),
	Died("§cDied");

	private String name;
	Status(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
