package net.devras.pit.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.devras.pit.Game;
import net.devras.pit.Statics;
import net.devras.pit.common.HoloGramManager;
import net.devras.pit.common.Status;
import net.devras.pit.common.v1_8_R3.EntityManager;
import net.devras.pit.event.PitEvent;

public class PitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§aThePit is running!");
			return true;
		}

		Player p = (Player) sender;

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("inLobby")) {
				p.sendMessage("§aYou are now inLobby? " + Game.lobbyRegion.isInRegion(p));
				return true;
			}

			if (args[0].equalsIgnoreCase("call")) {
				PitEvent.call();
				p.sendMessage("§aCalled");
				return true;
			}
			if (args[0].equalsIgnoreCase("lobby")) {
				Game.lobby = p.getLocation();
				p.sendMessage("§aSuccess set Lobby!");
				return true;
			}
			if (args[0].equalsIgnoreCase("hologram")) {
				Game.hologram = p.getLocation();
				p.sendMessage("§aSuccess set Hologram Location!");
				HoloGramManager.hologram.teleport(Game.hologram);
				return true;
			}
			if (args[0].equalsIgnoreCase("pos1")) {
				Game.lobbyRegion.setPos1(p.getLocation());
				p.sendMessage("§aSuccess set Pos1!");
				return true;
			}
			if (args[0].equalsIgnoreCase("pos2")) {
				Game.lobbyRegion.setPos2(p.getLocation());
				p.sendMessage("§aSuccess set Pos2!");
				return true;
			}

			if (args[0].equalsIgnoreCase("addSpawn")) {
				Game.RandomSpawns.add(p.getLocation());
				p.sendMessage("§aSuccess set Random Spawn Location");
				return true;
			}

			if (args[0].equalsIgnoreCase("addExp100")) {
				Statics.getStatics(p.getUniqueId()).addExp(100);
			}
			if (args[0].equalsIgnoreCase("bounty")) {
				Bukkit.broadcastMessage("§b" + p.getDisplayName() + " §7has won §6§lbounty §6§l$500§7!");
				Game.Bounty.put(p.getUniqueId(), true);
				Game.Status.put(p.getUniqueId(), Status.FIGHT);
				Statics.getStatics(p.getUniqueId()).setStreak(4);
				Statics.getStatics(p.getUniqueId()).addKills();
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				HoloGramManager.make();
				return true;
			}

			if (args[0].equalsIgnoreCase("makeNPCUpgrade")) {
				LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
				entity.setCustomName("§eUPGRADE");
				entity.setCustomNameVisible(true);
				entity.setRemoveWhenFarAway(false);
				EntityManager.setNoAI(entity);
			}
			if (args[0].equalsIgnoreCase("makeNPCItems")) {
				LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
				entity.setCustomName("§eITEMS");
				entity.setCustomNameVisible(true);
				entity.setRemoveWhenFarAway(false);
				EntityManager.setNoAI(entity);
			}

			if (args[0].equalsIgnoreCase("makeNPCSpawn")) {
				LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
				entity.setCustomName("§eSPAWN");
				entity.setCustomNameVisible(true);
				entity.setRemoveWhenFarAway(false);
				EntityManager.setNoAI(entity);
			}
			if (args[0].equalsIgnoreCase("makeNPCLobby")) {
				LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
				entity.setCustomName("§eLOBBY");
				entity.setCustomNameVisible(true);
				entity.setRemoveWhenFarAway(false);
				EntityManager.setNoAI(entity);
			}

		}
		return true;
	}

}
