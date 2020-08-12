package net.devras.pit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.devras.pit.Statics;
import net.devras.pit.common.EnderChestHandler;
import net.devras.pit.event.PitEvent;
import net.devras.pit.event.PitEvents;

public class ChestCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("I'm sorry, this command can execute only player in online. So you must logging in this server as player!");
			return true;
		}

		if (!PitEvent.getCurrentEvent().equals(PitEvents.NONE)) {
			sender.sendMessage("§cYou can not execute this command in pit event!");
			return true;
		}

		Player p = (Player) sender;
		Statics statics = Statics.getStatics(p.getUniqueId());

		if (!(statics.getLevel() >= 30)) {
			p.sendMessage("§cIf you want to perform this command, You have to execute this command to high level or rank.");
			p.sendMessage("§cSpecifically, 30 or up level or Moderator or up rank.");
			return true;
		}

		p.openInventory(EnderChestHandler.convert(p));

		return true;
	}

}
