package net.devras.pit.listener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.devras.pit.Assist;
import net.devras.pit.Game;
import net.devras.pit.Statics;
import net.devras.pit.common.ArmorEvent;
import net.devras.pit.common.DamageHandler;
import net.devras.pit.common.Perk;
import net.devras.pit.common.Status;
import net.devras.pit.common.TierType;
import net.devras.pit.util.ActionBar;
import net.devras.pit.util.SrvUtil;

public class CombatListener implements Listener{

	public HashMap<UUID, UUID> CoolTime = new HashMap<>();

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		UUID key = event.getPlayer().getUniqueId();
		if (CoolTime.containsKey(key)) {
			CoolTime.remove(key);
		}
	}

	@EventHandler
	public void onKill(EntityDeathEvent event) {
		event.getDrops().clear();
		event.setDroppedExp(0);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getCause().equals(DamageCause.FALL)) {
				event.setCancelled(true);
			}

			if (Game.lobbyRegion.isInRegion((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}

	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Assist.onDamage(event);
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			regCoolTime(event.getEntity().getUniqueId());
			regCoolTime(event.getDamager().getUniqueId());
			Game.Status.put(event.getEntity().getUniqueId(), Status.FIGHT);
			Game.Status.put(event.getDamager().getUniqueId(), Status.FIGHT);
		}

		if (event.getEntity() instanceof Player) {
			if (Game.lobbyRegion.isInRegion((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}

		if (event.getDamager() instanceof Projectile) {
			Projectile pro = (Projectile) event.getDamager();
			if (pro.getShooter() instanceof Player) {
				if (Game.lobbyRegion.isInRegion((Player) pro.getShooter())) {
					event.setCancelled(true);
				}else {
					Player p = (Player) pro.getShooter();
					Statics statics = Statics.getStatics(p.getUniqueId());

					if (event.getEntity() instanceof Player) {
						if (event.getEntity().getUniqueId().equals(p.getUniqueId())) {
							event.setCancelled(true);
						}
					}

					if (statics.havePark(Perk.ENDLESS)) {
						p.getInventory().addItem(new ItemStack(Material.ARROW, 3));
					}
					if (statics.havePark(Perk.VAMPIRE) && pro.getType().equals(EntityType.ARROW)) {
						if (20 > p.getHealth()) {
							try {
								p.setHealth(p.getHealth() + (event.getDamage() / 3));
							} catch (Exception e) {

							}
						}
					}
				}
			}
		}
		if (event.getDamager() instanceof Player) {
			if (Game.lobbyRegion.isInRegion((Player) event.getDamager())) {
				event.setCancelled(true);
			}else {
				Player p = (Player) event.getDamager();
				Statics statics = Statics.getStatics(p.getUniqueId());

				if (event.getEntity() instanceof Player) {
					Player target = (Player) event.getEntity();
					ActionBar bar = new ActionBar("§b" + target.getDisplayName() + " " + SrvUtil.getHeartIcon((int) target.getHealth(), (int) target.getMaxHealth()));
					bar.sendToPlayer(p);

					if (statics.havePark(Perk.Kung_FU)) {
						if (p.getInventory().getItemInHand() == null || p.getInventory().getItemInHand().getType().equals(Material.AIR)) {
							double damage = event.getDamage() * 2;
							if (damage > target.getHealth()) {
								damage = target.getHealth();
							}
							event.setDamage(damage);
						}
					}
				}


				if (statics.havePark(Perk.VAMPIRE)) {
					if (20 > p.getHealth()) {
						try {
							p.setHealth(p.getHealth() + 1);
						} catch (Exception e) {

						}
					}
				}
			}
		}

		DamageHandler.DamageCalc(event);
	}

	private void regCoolTime(final UUID to) {
		final UUID key = UUID.randomUUID();
		CoolTime.put(to, key);
		Bukkit.getScheduler().runTaskLaterAsynchronously(Game.Instance, new Runnable() {
			@Override
			public void run() {
				if (CoolTime.containsKey(to) && CoolTime.get(to).equals(key)) {
					CoolTime.remove(to);
					if (Game.Status.containsKey(to) && Game.Status.get(to).equals(Status.FIGHT)) {
						Game.Status.put(to, Status.IDLE);
						System.out.println(to.toString() + " IDLE");
					}
				}
			}
		}, 20L * 15);
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		event.setDeathMessage("");
		event.setKeepInventory(false);
		event.setKeepLevel(false);
		event.setNewExp(0);
		event.setNewLevel(0);
		event.setNewTotalExp(0);
		event.getDrops().clear();

		if (event.getEntity().getKiller() != null && event.getEntity().getUniqueId().equals(event.getEntity().getKiller().getUniqueId())) {
			return;
		}

		Assist.onDeath(event);

		final Player pl = event.getEntity();

		PlayerInventory inv = pl.getInventory();

		for (ItemStack item : inv.getContents()) {
			if (item != null) {
				if (item.getType().equals(Material.DIAMOND_SWORD)) {
					event.getDrops().add(item);
				}
				if (item.getType().equals(Material.OBSIDIAN)) {
					event.getDrops().add(item);
				}
				if (item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
					event.getDrops().add(item);
				}
				if (item.getType().equals(Material.DIAMOND_BOOTS)) {
					event.getDrops().add(item);
				}
			}
		}

		/*
		for (ItemStack item : event.getDrops()) {
			ItemStack i = item.clone();
			i.setAmount(1);
			pl.getWorld().dropItem(pl.getLocation(), i);
		}
		*/

		Game.Status.put(pl.getUniqueId(), Status.Died);

		if (pl.getKiller() != null) {
			Player p = pl.getKiller();

			Random r = new Random();
			Statics statics = Statics.getStatics(p.getUniqueId());

			int exp = r.nextInt(10);
			int gold = r.nextInt(10);
			exp = exp + (exp / 100 * statics.getTierLevel(TierType.XP));
			gold = gold + (gold / 100 * statics.getTierLevel(TierType.GOLD));

			statics.addExp(exp);
			statics.addGold(gold);
			statics.addKills();
			statics.executeSoul(-1);

			if (Game.Bounty.containsKey(pl.getUniqueId())) {
				statics.addGold(500);
				statics.addExp(300);
				Game.Bounty.remove(pl.getUniqueId());
			}

			if (statics.getStreak() >= 5) {
				statics.addExp(10);
			}
			if (statics.getStreak() >= 5 && !Game.Bounty.containsKey(p.getUniqueId())) {
				Game.BroadCast("§b" + p.getDisplayName() + " §7has won §6§lbounty §6§l$500§7!");
				Game.Bounty.put(p.getUniqueId(), true);
			}

			if ((statics.getStreak() % 5) == 0) {
				Game.BroadCast("§c§lSTREAK! §7of " + statics.getStreak() + " kills by §b" + p.getDisplayName());
			}

			if (statics.havePark(Perk.DIRTY)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 4, 1));
			}
			if (statics.havePark(Perk.STRENGTH)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 3, 1));
			}
			if (!statics.havePark(Perk.VAMPIRE)) {
				if (statics.havePark(Perk.GOLDEN_HEAD)) {
					p.getInventory().addItem(getGoldenHead());
				}else {
					p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
				}
			}else {
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0));
			}

			//pl.sendMessage("§eOpponent§7（§b" + p.getDisplayName() + "§7） §ehealth§7: §c" + ((int) p.getHealth()));
			//pl.sendMessage("§eDistance to opponent " + ((int) pl.getLocation().distance(p.getLocation())) + " blocks");
		}

		if (Game.enableKillCame) {
			final Location last = pl.getPlayer().getLocation();
			if (last.getBlockY() < 0) {
				last.setY(0);
			}

			Bukkit.getScheduler().runTaskLater(Game.Instance, new Runnable() {
				@Override
				public void run() {
					pl.spigot().respawn();
					pl.teleport(last.add(0, 2, 0));
					pl.setVelocity(last.getDirection().multiply(0.3f).setX(0.3f).setY(0.8f));
					pl.setGameMode(GameMode.SPECTATOR);
					Game.Status.put(pl.getUniqueId(), Status.Died);

					Statics statics = Statics.getStatics(pl.getUniqueId());
					statics.addDeath();
				}
			}, 1L);

			Bukkit.getScheduler().runTaskLater(Game.Instance, new Runnable() {
				@Override
				public void run() {
					pl.spigot().respawn();
					pl.teleport(Game.lobby);
					ArmorEvent.equip(pl);

					Game.Status.put(pl.getUniqueId(), Status.IDLE);
					pl.setHealth(20);
					pl.setGameMode(GameMode.SURVIVAL);
				}
			}, 20L * 3);
		}else {
			Bukkit.getScheduler().runTaskLater(Game.Instance, new Runnable() {
				@Override
				public void run() {
					pl.spigot().respawn();
					pl.teleport(Game.lobby);
					ArmorEvent.equip(pl);

					Statics statics = Statics.getStatics(pl.getUniqueId());
					statics.addDeath();

					Game.Status.put(pl.getUniqueId(), Status.IDLE);
				}
			}, 1L);
		}

		final Player p = event.getEntity();

		String newDeathMessage = "§b" + p.getDisplayName() + " §cwas slain!";

		if (p.getKiller() != null) {
			Player k = p.getKiller();
			newDeathMessage = "§b" + p.getDisplayName() + " §cwas slain by §b" + k.getDisplayName();
			if (p.getLastDamageCause() == null || p.getLastDamageCause().getCause() == null) {
				ItemStack item = k.getItemInHand();
				if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
					newDeathMessage += " §cusing [" + item.getItemMeta().getDisplayName() + "]";
				}
			}else {
				switch(p.getLastDamageCause().getCause()) {
				case PROJECTILE:
					ItemStack item = p.getItemInHand();
					newDeathMessage = "§b" + p.getName() + " §cwas shot by §b" + k.getName();
					if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
						newDeathMessage += " §cusing [" + item.getItemMeta().getDisplayName() + "]";
					}
					double distance = p.getLocation().distance(k.getLocation());
					BigDecimal distancebi = new BigDecimal(distance);
					double newdistance = distancebi.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
					newDeathMessage += " §c(§a" + newdistance + " §cblocks)";
					break;
				case FALL:
					newDeathMessage = p.getName() + " §cknocked off a cliff by §b" + k.getName() + "!";
					break;
				case LAVA:
					newDeathMessage = p.getName() + " §ctried to swim in lava to escape §b" + k.getName();
					break;
				case FIRE:
					newDeathMessage = p.getName() + " §cwalked into fire whilst fighting §b" + k.getName();
					break;
				default:
					break;
				}
			}
		}else {
			String messageSuffix = " §cwas slain!";
			if (p.getLastDamageCause() != null && p.getLastDamageCause().getCause() != null) {
				switch(p.getLastDamageCause().getCause()) {
				case FALL:
					messageSuffix = " §cfell to their death!";
					break;
				case SUFFOCATION:
					messageSuffix = " §csuffocated in a wall!";
					break;
				case LAVA:
					messageSuffix = " §ctried to swim in lava!";
					break;
				case FIRE:
					messageSuffix = " §cwalked into fire!";
					break;
				case DROWNING:
					messageSuffix = " §cdrowned";
					break;
				case ENTITY_EXPLOSION:
					messageSuffix = " §cexploded";
					break;
				case VOID:
					messageSuffix = " §cfell to the void!";
					break;
				default:
					break;
				}
			}
			newDeathMessage = p.getDisplayName() + messageSuffix;
		}

		event.setDeathMessage("§b" + newDeathMessage);
	}

	@EventHandler
	public void ProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow a = (Arrow) event.getEntity();
			a.remove();
		}
	}

	public static ItemStack getGoldenHead() {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setDisplayName("§6Golden Head");
		meta.setOwner("steave");
		skull.setItemMeta(meta);
		return skull;
	}
}
