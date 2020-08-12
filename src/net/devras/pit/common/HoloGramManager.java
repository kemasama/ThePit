package net.devras.pit.common;

import java.sql.ResultSet;

import org.bukkit.Bukkit;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.devras.pit.Game;
import net.devras.pit.Statics;

public class HoloGramManager {
	public static Hologram hologram = null;
	public static void make() {
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			return;
		}

		try {
			/**
			 * Load Data from MySQL
			 */

			if (hologram != null) {
				hologram.delete();
			}

			hologram = HologramsAPI.createHologram(Game.Instance, Game.hologram);

			hologram.clearLines();
			hologram.appendTextLine("§7-- §eRanking §7--");
			ResultSet res = Game.MySQL.query("select * from points order by prestage desc, Level desc limit 10");

			while (res != null && res.next()) {
				String format = String.format("%1$s §c%2$s §b%3$s", Statics.formatLevel(res.getInt("Level"), res.getInt("prestage")), res.getString("kills"), res.getString("name"));
				hologram.appendTextLine(format);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
