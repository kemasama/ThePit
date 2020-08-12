package net.devras.pit.event;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.devras.pit.Game;
import net.devras.pit.common.ArmorEvent;

public class PitEvent {
	private static PitEvents currentEvent = PitEvents.NONE;
	private static int oldRand = 0;
	
	public static void register() {
		Bukkit.getScheduler().runTaskTimer(Game.Instance, new Runnable() {
			@Override
			public void run() {
				call();
			}
		}, 0L, 20L * 60 * 3); // every 3 minutes
	}

	public static void call() {
		if (currentEvent.equals(PitEvents.NONE)) {
			chooseRandom();
		}else {
			fix();
		}
	}

	private static void fix() {
		PitEvents mode = PitEvents.valueOf(currentEvent.name());
		currentEvent = PitEvents.NONE;
		switch (mode) {
		case UHC:
			for (Player p : Bukkit.getOnlinePlayers()) {
				ArmorEvent.equip(p);
				p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1f, 1f);
			}
			break;
		default:
			break;
		}
	}

	private static void chooseRandom() {
		oldRand =+ 1;
		
		if (PitEvents.values().length < oldRand) {
			oldRand = 0;
		}
		
		currentEvent = PitEvents.values()[oldRand];

		if (currentEvent.equals(PitEvents.NONE)) {
			fix();
		}else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				ArmorEvent.equip(p);
				p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
				p.getWorld().strikeLightningEffect(p.getLocation());
			}
		}
	}

	public static PitEvents getCurrentEvent() {
		return currentEvent;
	}
	
	public static boolean currentUHCMode() {
		return currentEvent.equals(PitEvents.UHC);
	}
}
