package net.devras.pit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.devras.pit.Statics;

public class ChatEvent implements Listener{

	@EventHandler(priority=EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event){
		if (event.isCancelled()) {
			return;
		}

		Player p = event.getPlayer();
		Statics statics = Statics.getStatics(p.getUniqueId());

		String format = Statics.formatLevel(statics.getLevel(), statics.getPrestage()) + " §7%1$s: §7%2$s";

		event.setFormat(format);
	}
}
