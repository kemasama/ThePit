package net.devras.pit.common;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.devras.pit.Game;

public class Bounty implements Runnable{
	@Override
	public void run() {
		try {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (Game.Bounty.containsKey(pl.getUniqueId()) && Game.Bounty.get(pl.getUniqueId()) == true) {
					// Bounty
					
					Random rand = new Random();
					double x = Math.sqrt(Math.random() * 1) * (rand.nextBoolean() ? -1 : 1);
					double z = Math.sqrt(Math.random() * 1) * (rand.nextBoolean() ? -1 : 1);
					
					final Location loc = pl.getLocation().add(x, 0.1, z);
					final Hologram hologram = HologramsAPI.createHologram(Game.Instance, loc);
					hologram.appendTextLine("§6§l$500");
					Bukkit.getScheduler().runTaskLater(Game.Instance, new Runnable() {
						@Override
						public void run() {
							hologram.delete();
						}
					}, 10L);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
