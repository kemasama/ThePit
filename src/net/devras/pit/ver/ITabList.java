package net.devras.pit.ver;

import org.bukkit.entity.Player;

public interface ITabList {
	public void sendHeader(Player p, String header);
	public void sendFooter(Player p, String footer);
	public void sendHeaderFooter(Player p, String header, String footer);
}
