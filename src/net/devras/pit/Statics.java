package net.devras.pit;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.devras.pit.common.Perk;
import net.devras.pit.common.Tier;
import net.devras.pit.common.TierType;
import net.devras.pit.util.Title;

public class Statics {

	public static int defLevelExp = 15;
	private static HashMap<UUID, Statics> List = new HashMap<>();
	public static Statics getStatics(UUID key) {
		return List.containsKey(key) ? List.get(key) : new Statics(key);
	}
	public static void removeStatics(UUID key) {
		if (List.containsKey(key)) {
			List.get(key).Save();
			List.remove(key);
		}
	}
	public static int getTotalExp(int level, int exp) {
		int tl = exp;
		for (int i = 1; i < level; i++) {
			tl += (i * defLevelExp) + ((i - 1) * defLevelExp);
		}

		return tl;
	}

	public static String formatLevel(int level, int prestage) {
		String prefix = "";
		if (prestage > 20) {
			prefix = "§cXX";
		}else if (prestage > 19) {
			prefix = "§aXIX";
		}else if (prestage > 18) {
			prefix = "§aXVIII";
		}else if (prestage > 17) {
			prefix = "§aXVII";
		}else if (prestage > 16) {
			prefix = "§aXVI";
		}else if (prestage > 15) {
			prefix = "§aXV";
		}else if (prestage > 14) {
			prefix = "§aXIV";
		}else if (prestage > 13) {
			prefix = "§aXIII";
		}else if (prestage > 12) {
			prefix = "§aXII";
		}else if (prestage > 11) {
			prefix = "§aXI";
		}else if (prestage > 10) {
			prefix = "§aX";
		}else if (prestage > 9) {
			prefix = "§eIX";
		}else if (prestage > 8) {
			prefix = "§eVIII";
		}else if (prestage > 7) {
			prefix = "§eVII";
		}else if (prestage > 6) {
			prefix = "§eVI";
		}else if (prestage > 5) {
			prefix = "§eV";
		}else if (prestage > 4) {
			prefix = "§eIV";
		}else if (prestage > 3) {
			prefix = "§eIII";
		}else if (prestage > 2) {
			prefix = "§eII";
		}else if (prestage > 1) {
			prefix = "§eI";
		}

		if (!prefix.isEmpty()) {
			prefix = prefix.trim() + " ";
		}
		return prefix + formatLevel(level);
	}
	public static String formatLevel(int level) {

		String format = "§f[§7" + level + "§f]";

		if (level >= 120) {
			format = "§c[§4§k" + level + "§c]";
		}else if (level >= 100) {
			format = "§9[§d" + level + "§9]";
		}else if (level >= 90) {
			format = "§9[§5" + level + "§9]";
		}else if (level >= 80) {
			format = "§9[§4" + level + "§9]";
		}else if (level >= 70) {
			format = "§f[§c" + level + "§f]";
		}else if (level >= 60) {
			format = "§f[§6" + level + "§f]";
		}else if (level >= 50) {
			format = "§f[§e" + level + "§f]";
		}else if (level >= 40) {
			format = "§f[§a" + level + "§f]";
		}else if (level >= 30) {
			format = "§f[§2" + level + "§f]";
		}else if (level >= 20) {
			format = "§f[§9" + level + "§f]";
		}else if (level >= 10) {
			format = "§f[§1" + level + "§f]";
		}

		return format;
	}

	private int prestage = 1;
	private int Level = 1;
	private int Exp = 0;
	private int kills = 0;
	private int death = 0;
	private int gold = 0;
	private int streak = 0;
	private Player player;
	private UUID key;
	private Title title;
	private Perk parkA = Perk.NONE;
	private Perk parkB = Perk.NONE;
	private Perk parkC = Perk.NONE;
	private Tier goldTier;
	private Tier xpTier;
	private int MeleeDamage = 0;
	private int BowDamage = 0;
	private int DamageAmplification = 0;

	private int souls = 0;

	private Statics(UUID key) {

		this.player = Bukkit.getPlayer(key);
		this.key = key;
		this.title = new Title("§bLevel UP!!");
		this.goldTier = new Tier(0, TierType.GOLD);
		this.xpTier = new Tier(0, TierType.XP);

		try {
			ResultSet res = Game.MySQL.query(String.format("select * from points where uuid='%s';", key.toString()), true);

			while (res != null && res.next()) {
				this.Level = res.getInt("level");
				this.Exp = res.getInt("exp");
				this.setKills(res.getInt("kills"));
				this.setDeath(res.getInt("death"));
				this.setGold(res.getInt("gold"));
				this.setPrestage(res.getInt("prestage"));

				this.souls = kills;
			}

			ResultSet res2 = Game.MySQL.query(String.format("select * from perks where uuid='%s';", key.toString()), true);
			while (res2 != null && res2.next()) {
				parkA = Perk.getPark(res2.getString("a"), Perk.NONE);
				parkB = Perk.getPark(res2.getString("b"), Perk.NONE);
				parkC = Perk.getPark(res2.getString("c"), Perk.NONE);
			}

			ResultSet res3 = Game.MySQL.query("select * from tiers where uuid='" + key.toString() + "';", true);
			while (res3 != null && res3.next()) {
				goldTier.setLevel(res3.getInt("gold"));
				xpTier.setLevel(res3.getInt("xp"));
			}

			ResultSet res4 = Game.MySQL.query("select * from damage where uuid='" + key.toString() + "';", true);
			while (res4 != null && res4.next()) {
				this.setMeleeDamage(res4.getInt("melee"));
				this.setBowDamage(res4.getInt("bow"));
				this.setDamageAmplification(res4.getInt("reduce"));
			}

			ResultSet resSoul = Game.MySQL.query("select * from souls where uuid='" + key.toString() + "';", true);

			while (resSoul != null && resSoul.next()) {
				this.souls = resSoul.getInt("souls");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		List.put(key, this);
	}
	public void Save() {
		Game.MySQL.update(String.format("insert into points(uuid, name, level, exp, kills, death, gold, prestage)"
				+ " values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')"
				+ " on duplicate key update"
				+ " name=values(name), level=values(level), exp=values(exp), kills=values(kills), death=values(death), gold=values(gold), prestage=values(prestage);",
				key.toString(),
				(player != null ? player.getName() : "None"),
				Level,
				Exp,
				kills,
				death,
				gold,
				prestage), true);
		String query = String.format("insert into perks(uuid, a, b, c)"
				+ " values('%s', '%s', '%s', '%s')"
				+ " on duplicate key update"
				+ " a=values(a), b=values(b), c=values(c);", key.toString(), this.parkA.name(), this.parkB.name(), this.parkC.name());
		Game.MySQL.update(query);

		Game.MySQL.update("insert into tiers(uuid, gold, xp)"
				+ " values('" + key.toString() + "', " + goldTier.getLevel() + ", " + xpTier.getLevel() + ")"
						+ " on duplicate key update"
						+ " gold=values(gold), xp=values(xp);");
		// melee, bow, reduce
		Game.MySQL.update("insert into damage(uuid, melee, bow, reduce)"
				+ " values('" + key.toString() + "', " + getMeleeDamage() + ", " + getBowDamage() + ", " + getDamageAmplification() + ")"
						+ " on duplicate key update"
						+ " melee=values(melee), bow=values(bow), reduce=values(reduce);");
		Game.MySQL.update("insert into souls(uuid, souls)"
				+ " values('" + key.toString() + "', " + souls + ")"
				+ " on duplicate key update souls=values(souls)", true);
	}

	public int getTierLevel(TierType type) {
		if (type.equals(TierType.XP)) {
			return xpTier.getLevel();
		}
		if (type.equals(TierType.GOLD)) {
			return goldTier.getLevel();
		}

		return 0;
	}
	public void addTierLevel(TierType type) {
		if (type.equals(TierType.XP)) {
			xpTier.setLevel(xpTier.getLevel() + 1);
		}
		if (type.equals(TierType.GOLD)) {
			goldTier.setLevel(goldTier.getLevel() + 1);
		}
	}

	public boolean havePark(Perk park) {
		if (parkA.equals(park)) {
			return true;
		}
		if (parkB.equals(park)) {
			return true;
		}
		if (parkC.equals(park)) {
			return true;
		}
		return false;
	}

	public void addExp(int exp) {
		Exp += exp * Game.boostPer;
		if (exp > 0) {
			Exp += xpTier.toLevel(exp);
		}

		if (Exp >= getNeedExp()) {
			Exp = Exp - getNeedExp();

			Level += 1;
			if (Level > 120) {
				Level = 120;
			}

			if (player != null && player.isOnline()) {
				this.title.setSubtitle("§7[§b" + (Level - 1) + "§7] §e-> §7[§b" + Level + "§7]");
				this.title.send(player);
				MainTask.updatePlayerListName(player);

				player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
			}
		}

		Game.addExp(getPlayer(), exp);

	}

	public boolean Prestage() {
		if (Level >= 40) {
			this.prestage++;
			this.Level = 1;
			this.Exp = 0;
			this.streak = 0;
			this.setBowDamage(0);
			this.setMeleeDamage(0);
			this.setDamageAmplification(0);

			player.getEnderChest().clear();
			player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
			return true;
		}
		return false;
	}

	public void addGold(int amount) {
		gold += amount * Game.boostPer;
		if (amount > 0) {
			gold += goldTier.toLevel(amount);
		}
	}

	public void addKills() {
		kills++;
		addStreak();
	}
	public void addDeath() {
		death++;

		if (player != null && player.isOnline()) {
			Title title = new Title("§c§lYou died!");

			title.send(player);
			player.playSound(player.getLocation(), Sound.PIG_DEATH, 1f, 1f);
		}
		streak = 0;
	}

	public void addStreak() {
		streak++;
	}

	public int getNeedExp() {
		// if level = 2
		// 2 * 6 + 1 * 6
		// => 12 + 6 => 18
		return (Level * defLevelExp) + ((Level - 1) * defLevelExp);
	}

	public int getLevel() {
		return Level;
	}

	public void setLevel(int level) {
		Level = level;
	}

	public int getPrestage() {
		return prestage;
	}
	public void setPrestage(int prestage) {
		this.prestage = prestage;
	}
	public int getExp() {
		return Exp;
	}

	public void setExp(int exp) {
		Exp = exp;
	}

	public int getKills() {
		return kills;
	}
	public void setKills(int kills) {
		this.kills = kills;
	}
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}
	public int getDeath() {
		return death;
	}
	public void setDeath(int death) {
		this.death = death;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getStreak() {
		return streak;
	}
	public void setStreak(int streak) {
		this.streak = streak;
	}
	public Perk getParkA() {
		return parkA;
	}
	public void setParkA(Perk parkA) {
		this.parkA = parkA;
	}
	public Perk getParkB() {
		return parkB;
	}
	public void setParkB(Perk parkB) {
		this.parkB = parkB;
	}
	public Perk getParkC() {
		return parkC;
	}
	public void setParkC(Perk parkC) {
		this.parkC = parkC;
	}
	public int getMeleeDamage() {
		return MeleeDamage;
	}
	public void setMeleeDamage(int meleeDamage) {
		MeleeDamage = meleeDamage;
	}
	public int getBowDamage() {
		return BowDamage;
	}
	public void setBowDamage(int bowDamage) {
		BowDamage = bowDamage;
	}
	public int getDamageAmplification() {
		return DamageAmplification;
	}
	public void setDamageAmplification(int damageAmplification) {
		DamageAmplification = damageAmplification;
	}

	public int getSouls() {
		return souls;
	}
	public void setSouls(int souls) {
		this.souls = souls;
	}
	public void executeSoul(int amount) {
		souls = souls - amount;
		if (souls < 0) {
			souls = 0;
		}
	}
}
