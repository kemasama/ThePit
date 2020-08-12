package net.devras.pit.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import net.devras.pit.Game;

public class Jumper implements Listener{
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Block b = p.getLocation().subtract(0, 1, 0).getBlock();
		if (b.getType().equals(Material.BARRIER)) {
			p.teleport(Game.lobby);
		}

		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SLIME_BLOCK)) {
			p.setVelocity(p.getEyeLocation().getDirection().multiply(5).add(new Vector(0.0D, 0.2D, 0.0D)));
			p.playSound(p.getLocation(), Sound.FIREWORK_TWINKLE, 1f, 1f);
		}


		if (event.getTo().getBlockY() < -10) {
			//p.damage(p.getMaxHealth());
		}
	}
	
	
}
