package net.devras.pit.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EatHead implements Listener{
	@EventHandler
	public void onEatHead(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player p = event.getPlayer();
			ItemStack item = p.getItemInHand();
			if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
				return;
			}

			String name = item.getItemMeta().getDisplayName();
			if (name.startsWith("§c§l") && name.endsWith("'s Head")) {
				event.setCancelled(true);
				if (item.getAmount() == 1) {
					p.setItemInHand(new ItemStack(Material.AIR));
				}else if (item.getAmount() > 1) {
					ItemStack itemClone = item.clone();
					itemClone.setAmount(item.getAmount() - 1);
					p.setItemInHand(itemClone);
				}
				boolean giveSpeed = true;
				boolean giveRegen = true;
				int speedDuration = 20 * 20;
				int regenDuration = 7 * 20;
				if (p.getActivePotionEffects() != null) {
					for (PotionEffect pot : p.getActivePotionEffects()) {
						if (giveSpeed && pot.getType().equals(PotionEffectType.SPEED) && pot.getDuration() * 20 >= speedDuration) {
							giveSpeed = false;
							continue;
						}
						if (giveRegen && pot.getType().equals(PotionEffectType.REGENERATION) && pot.getDuration() * 20 >= regenDuration) {
							giveRegen = false;
							continue;
						}
					}
				}
				if (giveSpeed) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedDuration, 1));
				}
				if (giveRegen) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regenDuration, 1));
				}
				p.sendMessage("§aYou ate a player head and gained 7 seconds of Regeneration 2!");
			}
			if (name.equalsIgnoreCase("§6Golden Head")) {
				event.setCancelled(true);
				if (item.getAmount() == 1) {
					p.setItemInHand(new ItemStack(Material.AIR));
				}else if (item.getAmount() > 1) {
					ItemStack itemClone = item.clone();
					itemClone.setAmount(item.getAmount() - 1);
					p.setItemInHand(itemClone);
				}

				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9 * 20, 2));
				p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60 * 2, 0));

				p.sendMessage("§aYou ate a §6Golden Head §aand gained 9 seconds of Regeneration 3 and 2 minutes of Absorption!");
			}
		}
	}
}
