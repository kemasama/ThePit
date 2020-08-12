
package net.devras.pit;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.devras.pit.as.ArmorStands;
import net.devras.pit.common.Status;
import net.devras.pit.event.PitEvent;
import net.devras.pit.event.PitEvents;
import net.devras.pit.util.ScoreHelper;
import net.devras.pit.util.SrvUtil;

public class MainTask implements Runnable {

	public static MainTask Instance;
	public MainTask() {
		Instance = this;
	}

	public static void updatePlayerListName(Player p) {
		Statics statics = Statics.getStatics(p.getUniqueId());
		p.setPlayerListName(Statics.formatLevel(statics.getLevel(), statics.getPrestage()) + " §7" + p.getDisplayName());
	}

	@Override
	public void run() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
			Calendar calendar = Calendar.getInstance();
			String date = format.format(calendar.getTime());

			if (!Bukkit.isPrimaryThread()) {
				return;
			}

			for (Player p : Bukkit.getOnlinePlayers()) {
				ScoreHelper helper;
				if (ScoreHelper.hasScore(p)) {
					helper = ScoreHelper.getByPlayer(p);
				}else {
					helper = ScoreHelper.createScore(p);
				}

				Statics statics = Statics.getStatics(p.getUniqueId());

				helper.setTitle("§e§lThe Simple Pit");

				if (!PitEvent.getCurrentEvent().equals(PitEvents.NONE)) {
					helper.setSlot(11, "§7" + date + " S" + Game.ServerName);
					helper.setSlot(10, "§eEvent§7: §c§l" + PitEvent.getCurrentEvent().toName());
				}else {
					helper.removeSlot(11);
					helper.setSlot(10, "§7" + date + " S" + Game.ServerName);
				}
				helper.setSlot(9, "");

				helper.setSlot(8, "Level: " + Statics.formatLevel(statics.getLevel()));
				helper.setSlot(7, "Needed XP: §b" + (statics.getNeedExp() - statics.getExp()));
				helper.setSlot(6, "");
				helper.setSlot(5, "Gold: §6" + String.format("%,d", statics.getGold()) + "g");
				helper.setSlot(4, "Souls: §a" + statics.getSouls());
				helper.setSlot(3, "§fStatus: " + Game.Status.get(p.getUniqueId()).getName());

				if (statics.getStreak() > 0 && Game.Status.get(p.getUniqueId()).equals(Status.FIGHT)) {
					helper.setSlot(2, "Streak§f: §c" + statics.getStreak());
				}else {
					helper.setSlot(2, "");
				}
				helper.setSlot(1, "§emc.devras.info");

				if (p.isOp()) {
					SrvUtil.setPing(p, 1);
				}

				/**
				 * Tab List Header and Footer
				 */
				String version = SrvUtil.getServerVersion();
				try {
					Class<?> TabListClass = Class.forName("net.devras.pit.ver." + version + ".TabList");
					Object tabList = TabListClass.newInstance();
					Method how = tabList.getClass().getMethod("sendHeaderFooter", new Class[] {
							Player.class,
							String.class,
							String.class
					});
					how.invoke(tabList, new Object[] {
							p,
							"§eWelcome to Simple PVP Server",
							"§bYou are playing on §e§lMC.DEVRAS.INFO",
					});
				} catch (Exception e) {
					e.printStackTrace();
				}


				//ActionBar bar = new ActionBar("§bTotalExp " + Statics.getTotalExp(statics.getLevel(), statics.getExp()));
				//bar.sendToPlayer(p);

				for (Player pl : Bukkit.getOnlinePlayers()) {
					helper.setHealth(pl, (int) pl.getHealth());
				}

				if (Game.Bounty.containsKey(p.getUniqueId()) && Game.Bounty.get(p.getUniqueId())) {
					if (ArmorStands.isContains(p.getName())) {
						ArmorStands.getStand(p.getName()).teleport(p.getLocation().add(0, 2.3, 0));
					}else {
						ArmorStands.Make(p.getName(), p.getLocation().add(0, 2.3, 0));
					}
				}else {
					if (ArmorStands.isContains(p.getName())) {
						ArmorStands.Despawn(p.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
