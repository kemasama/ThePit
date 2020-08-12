package net.devras.pit;

import org.bukkit.entity.Player;

public class Language {
	public static String getLanguage(Player p) {
		return p.spigot().getLocale().split("_")[0];
	}

	public static void sendToFormat(Player p, String format) {
		if (getLanguage(p) == "en") {
			p.sendMessage(format);
		}

		System.out.println(p.spigot().getLocale());
	}

	public static enum MessageType{
		PRESTAGE_SUCCESS("§aSuccess prestaged!", "§aプレステージに成功しました！"),
		PRESTAGE_FAILED("§cFailed prestage!", "§cプレステージに失敗しました！"),
		ITEM_SUCCESS("§aThank you for purchasing!", "§aご購入ありがとうございます！"),
		ITEM_FAILED("§cYou don't have enough gold!", "§cゴールドが足りません！"),
		PERK_RESET("§cSuccess! Reset your perk!", "§cパークをリセットしました！");


		private String en, jp;
		private MessageType(String en, String jp) {
			this.en = en;
			this.jp = jp;
		}

		public String getFormat(Player p) {
			System.out.println("Language: " + p.getName() + " -> " + p.spigot().getLocale());
			if (p.spigot().getLocale().split("_")[0].equals("ja")) {
				return jp;
			}
			return en;
		}
	}
}
