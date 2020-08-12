package net.devras.pit.common;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.devras.pit.Statics;
import net.devras.pit.event.PitEvent;

public class ArmorEvent {
	public static void equip(Player target) {
		PlayerInventory inv = target.getInventory();

		inv.setArmorContents(null);
		inv.clear();

		if (PitEvent.currentUHCMode()) {
			ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
			sword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
			inv.setItem(0, sword);
	
			if (Statics.getStatics(target.getUniqueId()).havePark(Perk.FISHING_ROD)) {
				inv.setItem(1, new ItemStack(Material.FISHING_ROD));
			}
	
			if (Statics.getStatics(target.getUniqueId()).havePark(Perk.SAFETY_FIRST)) {
				inv.setHelmet(new ItemStack(Material.IRON_HELMET));
			}
	
			inv.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			inv.setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		}else {
			inv.setItem(0, new ItemStack(Material.IRON_SWORD));
	
			if (Statics.getStatics(target.getUniqueId()).havePark(Perk.FISHING_ROD)) {
				inv.setItem(1, new ItemStack(Material.FISHING_ROD));
				inv.setItem(2, new ItemStack(Material.BOW));
				inv.setItem(3, new ItemStack(Material.ARROW, 8));
			}else {
				inv.setItem(1, new ItemStack(Material.BOW));
				inv.setItem(2, new ItemStack(Material.ARROW, 8));
			}
	
			if (Statics.getStatics(target.getUniqueId()).havePark(Perk.SAFETY_FIRST)) {
				inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
			}
	
			inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			inv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
			inv.setBoots(new ItemStack(Material.IRON_BOOTS));
		}
	}
	
}
