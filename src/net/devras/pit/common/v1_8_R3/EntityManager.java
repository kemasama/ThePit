package net.devras.pit.common.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class EntityManager {
	public static void setNoAI(Entity entity) {
		try {
			net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) entity).getHandle();
	
			NBTTagCompound compound = new NBTTagCompound();
			nmsEn.c(compound);
			compound.setByte("NoAI", (byte) 1);
			compound.setByte("Invulnerable", (byte) 1);
			nmsEn.f(compound);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
