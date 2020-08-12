package net.devras.pit;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Assist {
	// UUID -> Damage
	// ArrayList<UUID> -> Damager
	private static Map<UUID, Map<UUID, Double>> Assists = new HashMap<>();

	public static void onDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player p = (Player) event.getEntity();
			Player d = (Player) event.getDamager();

			// Data aru?
			if (Assists.containsKey(p.getUniqueId())) {
				// Data attayoo
				// jaa UUID ha??
				if (Assists.get(p.getUniqueId()).containsKey(d.getUniqueId())) {
					// attayoo
					// damage tuikaa!
					Assists.get(p.getUniqueId()).put(d.getUniqueId(), Assists.get(p.getUniqueId()).get(d.getUniqueId()) + event.getDamage());
				}else {
					// sinki sakusei!
					Assists.get(p.getUniqueId()).put(d.getUniqueId(), event.getDamage());
				}
			}else {
				Assists.put(p.getUniqueId(), new HashMap<UUID, Double>());
			}
		}
	}

	public static void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		Player killer = p.getKiller();
		if (Assists.containsKey(p.getUniqueId())) {
			Random r = new Random();
			int rand = r.nextInt(15);
			try {
				for (UUID key : Assists.get(p.getUniqueId()).keySet()) {

					if (killer != null && killer.getUniqueId().equals(key)) {
						continue;
					}

					Player pl = Bukkit.getPlayer(key);
					if (pl != null && pl.isOnline()) {
						double totalDamage = Assists.get(p.getUniqueId()).get(key);
						double per = totalDamage / p.getMaxHealth();

						pl.sendMessage("§a§lASSIST! §7" + String.format("%.1f", per * 100) + "% §b" + p.getDisplayName());
						Statics statics = Statics.getStatics(key);
						statics.addExp(((int) per) / 10);
						statics.addGold(rand);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Assists.get(p.getUniqueId()).clear();
		}
	}

	public static void onQuit(PlayerQuitEvent event) {
		UUID key = event.getPlayer().getUniqueId();
		if (Assists.containsKey(key)) {
			Assists.remove(key);
		}
	}

}
