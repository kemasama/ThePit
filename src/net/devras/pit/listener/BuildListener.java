package net.devras.pit.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;

import net.devras.pit.Game;

public class BuildListener implements Listener {
	@EventHandler
	public void onBuild(BlockPlaceEvent event) {
		event.setCancelled(true);

		if (event.getBlock().getType().equals(Material.OBSIDIAN)) {
			final Location loc = event.getBlock().getLocation();
			if (!event.getBlockReplacedState().getType().equals(Material.AIR)) {
				return;
			}

			event.setCancelled(false);
			BukkitTask task = Bukkit.getScheduler().runTaskLater(Game.Instance, new Runnable() {
				@Override
				public void run() {
					loc.getBlock().setType(Material.AIR);
					int task = 0;
					for (int key : Game.Tasks.keySet()) {
						Location loc2 = Game.Tasks.get(key);
						if (loc2.getBlockX() == loc.getBlockX() && loc2.getBlockY() == loc.getBlockY() && loc2.getBlockZ() == loc.getBlockZ()) {
							task = key;
						}
					}

					if (Game.Tasks.containsKey(task)) {
						Game.Tasks.remove(task);
					}
				}
			}, 20L * 60 * 2);

			Game.Tasks.put(task.getTaskId(), loc);
		}

	}
	@EventHandler
	public void onBuild(BlockBreakEvent event) {
		event.setCancelled(true);
	}
}
