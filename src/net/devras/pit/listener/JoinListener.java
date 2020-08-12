package net.devras.pit.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.devras.pit.Assist;
import net.devras.pit.Game;
import net.devras.pit.MainTask;
import net.devras.pit.Statics;
import net.devras.pit.common.ArmorEvent;
import net.devras.pit.common.Status;
import net.devras.pit.util.ScoreHelper;

public class JoinListener implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		event.setJoinMessage("§f[§6INFO§f] §b" + p.getDisplayName() + " §ajoined the Pit.");

		ArmorEvent.equip(p);

		try {
			p.teleport(Game.lobby);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MainTask.updatePlayerListName(p);

		Game.Status.put(p.getUniqueId(), Status.IDLE);

		p.setGameMode(GameMode.SURVIVAL);
		ScoreHelper.createScore(p);

		Game.getServerNameQuery(p);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {

		Assist.onQuit(event);

		Player p = event.getPlayer();
		event.setQuitMessage("§f[§6INFO§f] §b" + p.getDisplayName() + " §aleft the Pit.");

		Statics.removeStatics(p.getUniqueId());
		if (ScoreHelper.hasScore(p)) {
			ScoreHelper.removeScore(p);
		}

		if (Game.Bounty.containsKey(p.getUniqueId())) {
			Game.Bounty.remove(p.getUniqueId());
		}
	}
}
