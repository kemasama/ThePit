package net.devras.pit.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.devras.pit.Game;

public class MessageListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subChannel = in.readUTF();

		System.out.println(subChannel);

		if (subChannel != null && subChannel.equalsIgnoreCase("GetServer")) {
			String serverName = in.readUTF();
			Game.ServerName = serverName.toUpperCase();
			System.out.println(serverName);
		}

	}

}
