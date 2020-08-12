package net.devras.pit.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Region {
	public final static int CHUNK_SIZE = 16;
	private String name = null;
	private World world = null;
	private Location pos1, pos2;
	private List<Block> Blocks = new ArrayList<>();

	public Region(String name) {
		this.name = name;
	}
	public Region(Location pos1, Location pos2, String name){
		if (pos1 == null || pos2 == null){
			return;
		}

		if (!pos1.getWorld().getName().equals(pos2.getWorld().getName())){
			return;
		}

		this.world = pos1.getWorld();

		this.name = name;

		this.pos1 = pos1;
		this.pos2 = pos2;
		Blocks = Region.getBlocks(pos1, pos2);
	}
	public Region(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
		this(new Location(w, x1, y1, z1), new Location(w, x2, y2, z2), w.getName());
	}

	public String getName(){
		if (this.name == null){
			this.name = this.world.getName();
		}
		return this.name;
	}
	public World getWorld(){
		return this.world;
	}

	public boolean isRegionBlock(Block block) {
		return Blocks.contains(block);
	}
	public boolean isInRegion(Player p) {
		Location loc = p.getLocation();

		if (!getWorld().getName().equals(loc.getWorld().getName())){
			return false;
		}

		for (Block block : Blocks) {
			if (block.getX() == loc.getBlockX() && block.getY() == loc.getBlockY() && block.getZ() == loc.getBlockZ()) {
				return true;
			}
		}

		return false;
	}

	public List<Block> getBlocks(){
		/*
		List<Block> blocks = new ArrayList<Block>();
		int x1 = this.pos1.getBlockX(), y1 = this.pos1.getBlockY(), z1 = this.pos1.getBlockZ();
		int x2 = this.pos2.getBlockX(), y2 = this.pos2.getBlockY(), z2 = this.pos2.getBlockZ();

		int lowestX = Math.min(x1, x2);
		int lowestY = Math.min(y1, y2);
		int lowestZ = Math.min(z1, z2);

		int highestX = lowestX == x1 ? x2 : x1;
		int highestY = lowestY == y1 ? y2 : y1;
		int highestZ = lowestZ == z1 ? z2 : z1;

		for (int x = lowestX; x <= highestX; x++){
			for (int y = lowestY; y <= highestY; y++){
				for (int z = lowestZ; z <= highestZ; z++){
					blocks.add(this.minCorner.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
		*/
		return Blocks;

	}

	public static List<Block> getBlocks(Location loc1, Location loc2){
		if (loc1.getWorld() != loc2.getWorld()) return null;
		World w = loc1.getWorld();
		List<Block> blocks = new ArrayList<>();

		int minX = (int) Math.min(loc1.getBlockX(), loc2.getBlockX());
		int minY = (int) Math.min(loc1.getBlockY(), loc2.getBlockY());
		int minZ = (int) Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int maxX = (int) Math.max(loc1.getBlockX(), loc2.getBlockX());
		int maxY = (int) Math.max(loc1.getBlockY(), loc2.getBlockY());
		int maxZ = (int) Math.max(loc1.getBlockZ(), loc2.getBlockZ());

		for (int x = minX; x <= maxX; x++){
			for (int y = minY; y <= maxY; y++){
				for (int z = minZ; z <= maxZ; z++){
					blocks.add(new Location(w, x, y, z).getBlock());
				}
			}
		}

		return blocks;
	}
	public Location getPos1() {
		return pos1;
	}
	public void setPos1(Location pos1) {
		this.pos1 = pos1;
		if (this.pos2 != null) {
			Blocks = Region.getBlocks(pos1, pos2);
		}
	}
	public Location getPos2() {
		return pos2;
	}
	public void setPos2(Location pos2) {
		this.pos2 = pos2;
		if (this.pos1 != null) {
			Blocks = Region.getBlocks(pos1, pos2);
		}
	}
}
