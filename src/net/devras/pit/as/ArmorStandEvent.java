package net.devras.pit.as;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class ArmorStandEvent implements Listener{
	@EventHandler
	public void onManipulate(PlayerArmorStandManipulateEvent event) {
		if (event.getRightClicked().isVisible()) {
			event.setCancelled(true);
		}
	}
}
