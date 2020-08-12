package net.devras.pit.event;

public enum PitEvents {
	NONE(""),
	UHC("UHC");
	
	private PitEvents(String name) {
		this.name = name;
	}
	
	private String name;
	public String toName() {
		return name;
	}
}
