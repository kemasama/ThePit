package net.devras.pit.common;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.devras.pit.Statics;

public class DamageHandler {

	public static void DamageCalc(EntityDamageByEntityEvent event) {
		double damage = event.getDamage();
		if (!(damage > 1)) {
			return;
		}

		if (event.getDamager() instanceof Player) {
			Player d = (Player) event.getDamager();
			Statics statics = Statics.getStatics(d.getUniqueId());
			int per = statics.getMeleeDamage();
			double dmg = event.getDamage() / 100 * per;
			damage = damage + dmg;
		}
		if (event.getDamager() instanceof Projectile) {
			Projectile pro = (Projectile) event.getDamager();
			if (pro.getShooter() instanceof Player) {
				Player p = (Player) pro.getShooter();
				Statics statics = Statics.getStatics(p.getUniqueId());
				int per = statics.getBowDamage();
				double dmg = event.getDamage() / 100 * per;
				damage = damage + dmg;

			}
		}

		if (event.getEntity() instanceof Player) {
			Player d = (Player) event.getEntity();
			Statics statics = Statics.getStatics(d.getUniqueId());
			int per = statics.getDamageAmplification();
			double dmg = event.getDamage() / 100 * per;
			damage = damage - dmg;
		}

		event.setDamage(damage);

	}

}
