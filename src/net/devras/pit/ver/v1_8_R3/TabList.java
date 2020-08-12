package net.devras.pit.ver.v1_8_R3;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.devras.pit.ver.ITabList;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public class TabList implements ITabList {

	@Override
	public void sendHeaderFooter(Player p, String header, String footer) {
		try {
			IChatBaseComponent Header = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + header + "\"}");
			IChatBaseComponent Footer = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + footer + "\"}");

			PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(Header);

			Field field = packet.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(packet, Footer);

			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendHeader(Player p, String header) {
	}
	@Override
	public void sendFooter(Player p, String footer) {
	}

}
