package net.devras.pit.common;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnderChestHandler {
	public static Inventory convert(Player p) {
		Inventory end = p.getEnderChest();
		Inventory inv = Bukkit.createInventory(null, end.getSize(), "§eEnderChest");

		for (int i = 0; i < end.getSize(); i++) {
			if (end.getItem(i) == null) {
				continue;
			}

			inv.setItem(i, end.getItem(i));
		}

		return inv;
	}

	public static void save(Player p, Inventory inv) {
		p.getEnderChest().clear();

		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				continue;
			}

			p.getEnderChest().setItem(i, inv.getItem(i));
		}
	}
}
