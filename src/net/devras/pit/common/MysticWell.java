package net.devras.pit.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.devras.pit.Statics;

public class MysticWell {
	public static void giveToPlayer(Player p) {
		Statics statics = Statics.getStatics(p.getUniqueId());
		if (!(statics.getPrestage() > 1)) {
			//p.sendMessage("§cYou don't have prestage level, it require 1 or up prestage level");
			//return;
			//p.playSound(p.getLocation(), Sound.PIG_DEATH, 1f, 1f);
		}

		if (statics.getSouls() >= 10) {
			statics.executeSoul(10);

			ItemStack item = getItem();
			p.playSound(p.getLocation(), Sound.EXPLODE, 1f, 1f);
			p.getInventory().addItem(item);
			p.sendMessage("§aYou received §e" + (item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name()) + " §a!");

			statics.addExp(10);

		}else {
			p.sendMessage("§cYou don't have enough souls, it require 10 or up souls to this item");
		}
	}

	private static ArrayList<ItemStack> Items = new ArrayList<>();
	public static void clearItems() {
		Items.clear();
	}
	public static void InitItems() {
		Items.add(makeItem(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, 1));
		Items.add(addEnchantment(makeItem(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 1), Enchantment.FIRE_ASPECT, 1));
		Items.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
		Items.add(makeItem(Material.BOW, Enchantment.ARROW_FIRE, 1));
		Items.add(makeItem(makeItem(Material.DIAMOND_AXE, Enchantment.DAMAGE_ALL, 3), "§9Perun Axe", "§eLightning Axe"));
		Items.add(makeItem(Material.CHAINMAIL_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, 1));

		Random r = new Random();
		{
			ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
			item.addUnsafeEnchantment(Enchantment.THORNS, 1);
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
			item.setItemMeta(meta);
			Items.add(item);
		}
		{
			ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
			item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
			item.setItemMeta(meta);
			Items.add(item);
		}
		{
			ItemStack item = new ItemStack(Material.LEATHER_HELMET);
			item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setColor(Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
			item.setItemMeta(meta);
			Items.add(item);
		}
	}
	public static ItemStack makeItem(Material type, Enchantment enchant, int level) {
		ItemStack item = new ItemStack(type);
		item.addUnsafeEnchantment(enchant, level);
		return item;
	}
	public static ItemStack addEnchantment(ItemStack item, Enchantment e, int l) {
		item.addUnsafeEnchantment(e, l);
		return item;
	}
	public static ItemStack makeItem(ItemStack item, String title, String... lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(title);
		meta.setLore(new ArrayList<>(Arrays.asList(lore)));
		item.setItemMeta(meta);
		return item;
	}

	private static ItemStack getItem() {
		Random r = new Random();

		ItemStack item = Items.get(r.nextInt(Items.size()));

		return item;
	}
}
