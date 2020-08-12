package net.devras.pit.as;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class ArmorStands {
	private static HashMap<String, ArmorStand> Stands = new HashMap<>();

	public static void removeAll(boolean force) {
		if (force) {
			for (String key : Stands.keySet()) {
				Stands.get(key).remove();
			}

			Stands.clear();
		}
	}

	public static boolean isContains(String name) {
		return Stands.containsKey(name);
	}
	public static ArmorStand getStand(String name) {
		return Stands.get(name);
	}

	public static void Make(String name, Location location) {
		ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		armorStand.setCanPickupItems(false);
		armorStand.setCustomName(name);
		armorStand.setCustomNameVisible(true);
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setRemoveWhenFarAway(true);

		Stands.put(ChatColor.stripColor(name), armorStand);
	}

	public static void Despawn(String name) {
		if (!isContains(name)) {
			return;
		}

		ArmorStand stand = getStand(name);
		stand.remove();
		Stands.remove(name);
	}
}
