package net.devras.pit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.devras.pit.as.ArmorStandEvent;
import net.devras.pit.as.ArmorStands;
import net.devras.pit.command.ChestCommand;
import net.devras.pit.command.PitCommand;
import net.devras.pit.command.SpawnCommand;
import net.devras.pit.common.HoloGramManager;
import net.devras.pit.common.MysticWell;
import net.devras.pit.common.Perk;
import net.devras.pit.common.Status;
import net.devras.pit.common.TierType;
import net.devras.pit.listener.BoosterListener;
import net.devras.pit.listener.BuildListener;
import net.devras.pit.listener.ChatEvent;
import net.devras.pit.listener.CombatListener;
import net.devras.pit.listener.EatHead;
import net.devras.pit.listener.Events;
import net.devras.pit.listener.JoinListener;
import net.devras.pit.listener.Jumper;
import net.devras.pit.listener.MessageListener;
import net.devras.pit.listener.OpenShopListener;
import net.devras.pit.util.CustomConfig;
import net.devras.pit.util.IconMenu;
import net.devras.pit.util.IconMenu.Row;
import net.devras.pit.util.IconMenu.onClick;
import net.devras.pit.util.Region;
import net.md_5.bungee.api.chat.BaseComponent;


public class Game extends JavaPlugin {
	public static Game Instance;
	public static CMySQL MySQL;
	public static FileConfiguration config;
	public static CustomConfig goldSpawnConfig;
	public static Location lobby;
	public static Location hologram;
	public static Region lobbyRegion;
	public static ArrayList<Location> RandomSpawns = new ArrayList<>();

	public static boolean enableKillCame = true;

	public static HashMap<UUID, Status> Status = new HashMap<>();
	public static HashMap<UUID, Boolean> Bounty = new HashMap<>();
	public static HashMap<Integer, Location> Tasks = new HashMap<>();

	public static IconMenu shop, parkShop, PreStageShop;

	public static String ServerName = "PIT";
	public static int boostPer = 1;

	@Override
	public void onDisable() {

		/**
		 * SaveConfig
		 */

		config = getConfig();
		config.set("lobby", lobby);
		config.set("hologram", hologram);
		config.set("pos1", lobbyRegion.getPos1());
		config.set("pos2", lobbyRegion.getPos2());
		//config.set("spawns", RandomSpawns);
		config.set("killCame", enableKillCame);

		goldSpawnConfig.getConfig().set("spawns", RandomSpawns);
		goldSpawnConfig.saveConfig();

		saveConfig();

		if (MySQL != null && MySQL.isConnected()) {
			MySQL.disConnect();
			MySQL = null;
			getLogger().info("Disconnected MySQL!");
		}


		for (int key : Tasks.keySet()) {
			Location loc = Tasks.get(key);
			loc.getBlock().setType(Material.AIR);
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.kickPlayer("Server will restarting!");
		}

		Status.clear();
		Bounty.clear();
		Tasks.clear();

		Game.ServerName = "PIT";

		ArmorStands.removeAll(true);
		MysticWell.clearItems();

		super.onDisable();
	}

	@Override
	public void onEnable() {

		/**
		 * Set Instance
		 */
		Instance = this;

		/**
		 * Load Config
		 */
		saveDefaultConfig();
		config = getConfig();

		/**
		 * Load Settings from Config
		 */

		if (config.get("lobby") instanceof Location) {
			lobby = (Location) config.get("lobby");
		}else {
			lobby = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		if (config.get("hologram") instanceof Location) {
			hologram = (Location) config.get("hologram");
		}else {
			hologram = Bukkit.getWorlds().get(0).getSpawnLocation();
		}

		enableKillCame = config.getBoolean("killCame", false);

		Location pos1, pos2;
		if (config.get("pos1") instanceof Location) {
			pos1 = ((Location) config.get("pos1"));
		}else {
			pos1 = (Bukkit.getWorlds().get(0).getSpawnLocation());
		}

		if (config.get("pos2") instanceof Location) {
			pos2 = ((Location) config.get("pos2"));
		}else {
			pos2 = (Bukkit.getWorlds().get(0).getSpawnLocation());
		}

		lobbyRegion = new Region(pos1, pos2, "LOBBY");

		goldSpawnConfig = new CustomConfig("golds.yml");
		FileConfiguration config2 = goldSpawnConfig.getConfig();
		if (config2.contains("spawns")) {
			for (Object obj : config2.getList("spawns")) {
				if (obj instanceof Location) {
					RandomSpawns.add((Location) obj);
				}
			}
		}

		/**
		 * Initialize MySQL
		 */
		MySQL = new CMySQL(config.getString("mysql.host"), config.getString("mysql.port"), config.getString("mysql.name"), config.getString("mysql.user"), config.getString("mysql.pass"));
		MySQL.Connect();

		if (MySQL.isConnected()) {
			getLogger().info("Connected MySQL!");
			if (!MySQL.tableExists("points")) {
				MySQL.createTable("points", "uuid varchar(36) not null primary key, name varchar(16) not null, level int not null default 1, exp int not null default 0, kills int not null default 0, death int not null default 0, gold int not null default 0, prestage int not null default 1");
				getLogger().info("Make a Point table");
			}
			if (!MySQL.tableExists("perks")) {
				MySQL.createTable("perks", "uuid varchar(36) not null primary key, a varchar(20), b varchar(20), c varchar(20)");
				getLogger().info("Make a Perks table");
			}
			if (!MySQL.tableExists("tiers")) {
				MySQL.createTable("tiers", "uuid varchar(36) not null primary key, gold int not null default 0, xp int not null default 0");
				getLogger().info("Make a Tiers table");
			}
			if (!MySQL.tableExists("damage")) {
				MySQL.createTable("damage", "uuid varchar(36) not null primary key, melee int not null default 0, bow int not null default 0, reduce int not null default 0");
				getLogger().info("Make a Damage table");
			}

			if (!MySQL.tableExists("souls")) {
				MySQL.createTable("souls", "uuid varchar(36) not null primary key, souls int not null default 0");
			}
		}else {
			getLogger().info("Can not connect MySQL!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/**
		 * Register Events
		 */

		Bukkit.getPluginManager().registerEvents(new BuildListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatEvent(), this);
		Bukkit.getPluginManager().registerEvents(new CombatListener(), this);
		Bukkit.getPluginManager().registerEvents(new EatHead(), this);
		Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new Jumper(), this);
		Bukkit.getPluginManager().registerEvents(new OpenShopListener(), this);
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "NL");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "NL", new BoosterListener());
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());

		/**
		 * Register Command
		 */
		Bukkit.getPluginCommand("pit").setExecutor(new PitCommand());
		Bukkit.getPluginCommand("spawn").setExecutor(new SpawnCommand());
		Bukkit.getPluginCommand("chest").setExecutor(new ChestCommand());

		/**
		 * Register Game Timer
		 */
		Bukkit.getScheduler().runTaskTimer(this, new MainTask(), 0L, 20L);
		//Bukkit.getScheduler().runTaskTimer(this, new Bounty(), 0L, 20L);
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				try {
					for (Player p : Bukkit.getOnlinePlayers()) {
						Statics.getStatics(p.getUniqueId()).Save();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				HoloGramManager.make();
			}
		}, 0L, 20L * 60L * 3);
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				for (Location loc : RandomSpawns) {
					try {
						int count = 0;
						for (Entity entity : loc.getWorld().getChunkAt(loc).getEntities()) {
							if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
								Item item = (Item) entity;
								if (!item.getItemStack().getType().equals(Material.GOLD_INGOT)) {
									count++;
								}
							}
						}
						if (count < 3) {
							loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.GOLD_INGOT));
							break;
						}
					} catch (Exception e) {}
				}
			}
		}, 0L, 20L * 60 * 3);

		HoloGramManager.make();
		MysticWell.InitItems();

		/**
		 * Register Shop
		 */
		register();
		//PitEvent.register();

		saveConfig();
		config = null;

		Bukkit.getPluginManager().registerEvents(new ArmorStandEvent(), this);

		/**
		 * Show console
		 */
		super.onEnable();
	}

	public static void addExp(Player p, int amount) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(p.getName());
			out.writeUTF(p.getUniqueId().toString());
			out.writeUTF(String.valueOf(amount));
			p.sendPluginMessage(Game.Instance, "NL", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void sendLobby(Player p) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("lobby");
			p.sendPluginMessage(Game.Instance, "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getServerNameQuery(Player p) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServer");
			p.sendPluginMessage(Game.Instance, "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void register() {
		PreStageShop = new IconMenu("§ePreStage", 1, new onClick() {
			@Override
			public boolean click(Player p, IconMenu menu, Row row, int slot, ItemStack item) {
				Statics statics = Statics.getStatics(p.getUniqueId());
				if (row.getRow() == 0) {
					if (slot == 4) {
						if (statics.Prestage()) {
							p.sendMessage(Language.MessageType.PRESTAGE_SUCCESS.getFormat(p));
						}else {
							p.sendMessage(Language.MessageType.PRESTAGE_FAILED.getFormat(p));
						}
					}
				}
				return false;
			}
		});
		PreStageShop.addButton(PreStageShop.getRow(0), 4, new ItemStack(Material.NETHER_STAR), "Prestage", "Required 40 Level");

		shop = new IconMenu("§eItems", 3, new onClick() {
			@Override
			public boolean click(Player p, IconMenu menu, Row row, int slot, ItemStack item) {

				Statics statics = Statics.getStatics(p.getUniqueId());

				if (row.getRow() == 1) {
					if (slot == 1) {
						if (statics.getGold() >= 150) {
							statics.setGold(statics.getGold() - 150);
							p.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));
							p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
							return true;
						}else {
							p.sendMessage(Language.MessageType.ITEM_FAILED.getFormat(p));
							return true;
						}
					}
					if (slot == 3) {
						if (statics.getGold() >= 50) {
							statics.setGold(statics.getGold() - 50);
							p.getInventory().addItem(new ItemStack(Material.OBSIDIAN, 8));
							p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
							return true;
						}else {
							p.sendMessage(Language.MessageType.ITEM_FAILED.getFormat(p));
							return true;
						}
					}
					if (slot == 5) {
						if (statics.getGold() >= 500) {
							statics.setGold(statics.getGold() - 500);
							p.getInventory().addItem(new ItemStack(Material.DIAMOND_CHESTPLATE));
							p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
							return true;
						}else {
							p.sendMessage(Language.MessageType.ITEM_FAILED.getFormat(p));
							return true;
						}
					}
					if (slot == 7) {
						if (statics.getGold() >= 300) {
							statics.setGold(statics.getGold() - 300);
							p.getInventory().addItem(new ItemStack(Material.DIAMOND_BOOTS));
							p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
							return true;
						}else {
							p.sendMessage(Language.MessageType.ITEM_FAILED.getFormat(p));
							return true;
						}
					}
				}

				return true;
			}
		});

		// diamond sword
		// obsidin x8
		// diamond chestplate
		// diamond boots
		shop.addButton(shop.getRow(1), 1, new ItemStack(Material.DIAMOND_SWORD), "DiamondSword", "Sold 150g");
		shop.addButton(shop.getRow(1), 3, new ItemStack(Material.OBSIDIAN), "Obsidian", "Sold 50g");
		shop.addButton(shop.getRow(1), 5, new ItemStack(Material.DIAMOND_CHESTPLATE), "DiamondChestplate", "Sold 500g");
		shop.addButton(shop.getRow(1), 7, new ItemStack(Material.DIAMOND_BOOTS), "DiamondBoots", "Sold 300g");

		parkShop = new IconMenu("§ePerk", 6, new onClick() {
			@Override
			public boolean click(Player p, IconMenu menu, Row row, int slot, ItemStack item) {
				Statics statics = Statics.getStatics(p.getUniqueId());

				int gold = 1000;
				/*
				String command = "lp user " + p.getName() + " permission set " + permission + " true";
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				*/

				if (row.getRow() == 5 && slot == 4) {

					if (statics.havePark(Perk.FISHING_ROD)) {
						p.getInventory().remove(Material.FISHING_ROD);
					}
					if (statics.havePark(Perk.SAFETY_FIRST)) {
						p.getInventory().remove(Material.CHAINMAIL_HELMET);
					}

					statics.setParkA(Perk.NONE);
					statics.setParkB(Perk.NONE);
					statics.setParkC(Perk.NONE);
					p.sendMessage(Language.MessageType.PERK_RESET.getFormat(p));
					return false;
				}

				if (row.getRow() == 5) {
					int baseGold = 300;

					if (slot == 1) {
						int dmg = statics.getMeleeDamage();
						int dgold = dmg * 3000;
						if (statics.getGold() >= dgold) {
							statics.addGold(dgold * -1);
							statics.setMeleeDamage(dmg + 1);
							p.sendMessage("§aSuccess Increase sword " + statics.getMeleeDamage() + " damage!");
						}else {
							p.sendMessage("§cYou don't have enough gold!");
						}

						return true;
					}
					if (slot == 2) {
						int dmg = statics.getBowDamage();
						int dgold = dmg * 3000;
						if (statics.getGold() >= dgold) {
							statics.addGold(dgold * -1);
							statics.setBowDamage(dmg + 1);
							p.sendMessage("§aSuccess Increase bow " + statics.getBowDamage() + " damage!");
						}else {
							p.sendMessage("§cYou don't have enough gold!");
						}

						return true;
					}
					if (slot == 3) {
						int dmg = statics.getDamageAmplification();
						int dgold = dmg * 3000;
						if (statics.getGold() >= dgold) {
							statics.addGold(dgold * -1);
							statics.setDamageAmplification(dmg + 1);
							p.sendMessage("§aSuccess Reduce " + statics.getDamageAmplification() + " damage!");
						}else {
							p.sendMessage("§cYou don't have enough gold!");
						}

						return true;
					}

					if (slot == 5) {
						p.sendMessage("§aYour perk: " + statics.getParkA().name() + ", " + statics.getParkB().name() + ", " + statics.getParkC().name());
						return false;
					}

					TierType type = null;

					if (slot == 6) {
						type = TierType.XP;
					}
					if (slot == 7) {
						type = TierType.GOLD;
					}

					if (type == null) {
						return true;
					}

					int needGold = (int) Math.pow(statics.getTierLevel(type) * baseGold, 2) + baseGold;
					if (statics.getGold() >= needGold) {
						statics.addGold(needGold * -1);
						statics.addTierLevel(type);
						p.sendMessage("§aSuccessfully level up your tier!");
						return true;
					}else {
						p.sendMessage("§cYou don't have enough gold!");
						return false;
					}
				}

				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
					Perk park = Perk.getPark(item.getItemMeta().getDisplayName(), Perk.NONE);
					gold = park.getGold();

					if (!park.equals(Perk.NONE)) {
						if (statics.havePark(park)) {
							p.sendMessage("§cYou have already perk!");
							p.sendMessage("§cSo remove it from your perk list!");
							if (statics.getParkA().equals(park)) {
								statics.setParkA(Perk.NONE);
							}
							if (statics.getParkB().equals(park)) {
								statics.setParkB(Perk.NONE);
							}
							if (statics.getParkC().equals(park)) {
								statics.setParkC(Perk.NONE);
							}
							return true;
						}

						String perm = "park." + park.name();
						if (!p.hasPermission(perm)) {
							if (statics.getGold() >= gold) {
								statics.setGold(statics.getGold() - gold);
								String command = "lp user " + p.getName() + " permission set " + perm + " true";
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

								if (park.equals(Perk.FISHING_ROD)) {
									p.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
								}
								if (park.equals(Perk.SAFETY_FIRST)) {
									p.getInventory().addItem(new ItemStack(Material.CHAINMAIL_HELMET));
								}

								if (statics.getParkA().equals(Perk.NONE)) {
									statics.setParkA(park);
									p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
									return true;
								}
								if (statics.getParkB().equals(Perk.NONE)) {
									statics.setParkB(park);
									p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
									return true;
								}
								if (statics.getParkC().equals(Perk.NONE)) {
									statics.setParkC(park);
									p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
									return true;
								}
								p.sendMessage(Language.MessageType.ITEM_SUCCESS.getFormat(p));
								p.sendMessage("§aBut your perk list is full!");
								p.sendMessage("§aSo if you want to set a perk");
								p.sendMessage("§ayou have to reset your perk list!");
								return true;
							}else {
								p.sendMessage("§cYou don't have enough gold!");
								return true;
							}
						}else {
							if (statics.getParkA().equals(Perk.NONE)) {
								statics.setParkA(park);
								p.sendMessage("§aThank you for purchasing!");
								if (park.equals(Perk.FISHING_ROD)) {
									p.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
								}
								if (park.equals(Perk.SAFETY_FIRST)) {
									p.getInventory().addItem(new ItemStack(Material.CHAINMAIL_HELMET));
								}

								return true;
							}
							if (statics.getParkB().equals(Perk.NONE)) {
								statics.setParkB(park);
								p.sendMessage("§aThank you for purchasing!");
								if (park.equals(Perk.FISHING_ROD)) {
									p.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
								}
								if (park.equals(Perk.SAFETY_FIRST)) {
									p.getInventory().addItem(new ItemStack(Material.CHAINMAIL_HELMET));
								}

								return true;
							}
							if (statics.getParkC().equals(Perk.NONE)) {
								statics.setParkC(park);
								p.sendMessage("§aThank you for purchasing!");
								if (park.equals(Perk.FISHING_ROD)) {
									p.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
								}
								if (park.equals(Perk.SAFETY_FIRST)) {
									p.getInventory().addItem(new ItemStack(Material.CHAINMAIL_HELMET));
								}

								return true;
							}
							p.sendMessage("§aThank you for purchasing!");
							p.sendMessage("§aBut your perk list is full!");
							p.sendMessage("§aSo if you want to set a perk");
							p.sendMessage("§ayou have to reset your perk list!");
							return true;
						}
					}
				}

				return true;
			}
		});

		parkShop.addButton(parkShop.getRow(1), 1, CombatListener.getGoldenHead(), Perk.GOLDEN_HEAD.name(), "Sold " + Perk.GOLDEN_HEAD.getGold());
		parkShop.addButton(parkShop.getRow(1), 3, new ItemStack(Material.FISHING_ROD), Perk.FISHING_ROD.name(), "Sold " + Perk.FISHING_ROD.getGold());
		parkShop.addButton(parkShop.getRow(1), 5, new ItemStack(Material.REDSTONE), Perk.VAMPIRE.name(), "Sold " + Perk.VAMPIRE.getGold());
		parkShop.addButton(parkShop.getRow(1), 7, new ItemStack(Material.DIAMOND_SWORD), Perk.STRENGTH.name(), "Sold " + Perk.STRENGTH.getGold());

		parkShop.addButton(parkShop.getRow(2), 1, new ItemStack(Material.CHAINMAIL_HELMET), Perk.SAFETY_FIRST.name(), "Sold " + Perk.SAFETY_FIRST.getGold());
		parkShop.addButton(parkShop.getRow(2), 3, new ItemStack(Material.IRON_DOOR), Perk.DIRTY.name(), "Sold " + Perk.DIRTY.getGold());
		parkShop.addButton(parkShop.getRow(2), 5, new ItemStack(Material.BOW), Perk.ENDLESS.name(), "Sold " + Perk.ENDLESS.getGold());
		parkShop.addButton(parkShop.getRow(2), 7, new ItemStack(Material.PUMPKIN), Perk.Kung_FU.name(), "Sold " + Perk.Kung_FU.getGold());

		parkShop.addButton(parkShop.getRow(5), 1, new ItemStack(Material.DIAMOND_SWORD), "Melee Damage", "Increase sword damage");
		parkShop.addButton(parkShop.getRow(5), 2, new ItemStack(Material.BOW), "Bow Damage", "Increase bow damage");
		parkShop.addButton(parkShop.getRow(5), 3, new ItemStack(Material.DIAMOND_CHESTPLATE), "Damage Amplification", "Reduce damage");

		parkShop.addButton(parkShop.getRow(5), 4, new ItemStack(Material.BARRIER), "Disable", "Reset All Perk");

		parkShop.addButton(parkShop.getRow(5), 5, new ItemStack(Material.ANVIL), "PERK LIST", "Show your perk list!");
		parkShop.addButton(parkShop.getRow(5), 6, new ItemStack(Material.EXP_BOTTLE), "XP Tier", "Level Up your XP Tier");
		parkShop.addButton(parkShop.getRow(5), 7, new ItemStack(Material.GOLD_INGOT), "Gold Tier", "Level Up your Gold Tier");
	}

	public static void BroadCast(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(message);
		}
	}
	public static void BroadCast(BaseComponent component) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.spigot().sendMessage(component);
		}
	}

}
