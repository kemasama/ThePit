package net.devras.pit.listener;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.devras.pit.Game;
import net.devras.pit.common.EnderChestHandler;
import net.devras.pit.common.MysticWell;
import net.devras.pit.common.RandomLocation;

public class OpenShopListener implements Listener {
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		if (event.getRightClicked().getType().equals(EntityType.VILLAGER)) {
			event.setCancelled(true);
			if (event.getRightClicked().getCustomName() == null) {
				return;
			}

			if (event.getRightClicked().getCustomName().equals("§eUPGRADE")) {
				Game.parkShop.open(p);
			}
			if (event.getRightClicked().getCustomName().equals("§eITEMS")) {
				Game.shop.open(p);
			}
			if (event.getRightClicked().getCustomName().equals("§eSPAWN")) {
				p.teleport(RandomLocation.getRandSpawm());
			}
			if (event.getRightClicked().getCustomName().equals("§eLOBBY")) {
				Game.sendLobby(p);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity().getType().equals(EntityType.VILLAGER)) {
			if (event.getEntity().getCustomName() != null) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onOpen(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if (event.getClickedBlock() != null) {
			if (event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)) {
				openConfirmMysticWell(p);
			}
		}
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = false)
	public void onOpen(InventoryOpenEvent event) {
		Player p = (Player) event.getPlayer();
		
		if (event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
			event.setCancelled(true);
			Game.PreStageShop.open(p);
			//Game.shop.open(p);
			return;
		}
		if (event.getInventory().getType().equals(InventoryType.ENCHANTING)) {
			event.setCancelled(true);
			
			openConfirmMysticWell(p);

			//p.sendMessage("§aYou opend confirm Menu!");
			//MysticWell.giveToPlayer(p);
			return;
		}
	}
	public void openConfirmMysticWell(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9, "§eConfirm MysticWell");

		//         c
		// 0 1 2 3 4 5 6 7 8

		inv.setItem(2, makeItem(Material.EMERALD_BLOCK, "§aConfirm", "§eYou need 10 souls in open MysticWell."));
		inv.setItem(6, makeItem(Material.BARRIER, "§cCancel", "§cCancel MysticWell"));
		
		p.openInventory(inv);
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equalsIgnoreCase("§eConfirm MysticWell")) {
			event.setCancelled(true);
			if (event.getSlot() == 2) {
				MysticWell.giveToPlayer((Player) event.getWhoClicked());
			}
			event.getWhoClicked().closeInventory();
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (event.getInventory().getTitle().equals("§eEnderChest")) {
			EnderChestHandler.save((Player) event.getPlayer(), event.getInventory());
		}
	}

	private static ItemStack makeItem(Material type, String title, String... lore) {
		ItemStack item = new ItemStack(type);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(new ArrayList<>(Arrays.asList(lore)));
		item.setItemMeta(meta);
		return item;
	}
}
