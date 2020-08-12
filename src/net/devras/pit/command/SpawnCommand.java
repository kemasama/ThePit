package net.devras.pit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.devras.pit.Game;
import net.devras.pit.common.Status;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;
		if (!Game.Status.containsKey(p.getUniqueId())) {
			return true;
		}

		if (Game.Status.get(p.getUniqueId()).equals(Status.IDLE)) {
			if (!Game.lobbyRegion.isInRegion(p)) {
				p.teleport(Game.lobby);
				Game.BroadCast(String.format("§b%s §aback lobby!", p.getDisplayName()));
			}else {
				p.sendMessage("§cYou are already in lobby");
			}
		}else {
			p.sendMessage("§cYou are not idle!");
		}

		return true;
	}

}
