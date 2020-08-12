package net.devras.pit.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import net.devras.pit.Game;
import net.devras.pit.Statics;


public class Events implements Listener{
	@EventHandler(ignoreCancelled = true)
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(event.toWeatherState());
		event.getWorld().setWeatherDuration(0);
		event.getWorld().setThundering(false);
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItem().getItemStack();

		if (item.getType().equals(Material.GOLD_INGOT)) {
			Statics statics = Statics.getStatics(p.getUniqueId());
			int gold = ((new Random()).nextInt(10)) + 1;

			if (gold > 0) {
				statics.addGold(gold * item.getAmount());

				p.sendMessage("§6§lGOLD PICKUP! §7from the ground §6" + gold + "g");

				final Player pl = p.getPlayer();
				Bukkit.getScheduler().runTaskLater(Game.Instance, new Runnable() {
					@Override
					public void run() {
						pl.getInventory().remove(Material.GOLD_INGOT);
					}
				}, 10L);
				if (event.isCancelled()) {
					event.setCancelled(false);
				}
			}
		}
	}


	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	@EventHandler
	public void onDamageItem(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void EntityExplodeEvent(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void CreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
}
