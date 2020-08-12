package net.devras.pit.common;

import java.util.Random;

import org.bukkit.Location;

import net.devras.pit.Game;

public class RandomLocation {
	public static int oldRand = 0;
	public static Location getRandSpawm() {
		Random r = new Random();
		int rand = 0;
		int size = Game.RandomSpawns.size();

		do {
			rand = r.nextInt(size);
		} while (rand == oldRand);

		oldRand = rand;

		return Game.RandomSpawns.get(rand);
	}
}
