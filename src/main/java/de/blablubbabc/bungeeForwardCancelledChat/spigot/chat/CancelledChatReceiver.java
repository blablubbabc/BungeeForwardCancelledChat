package de.blablubbabc.bungeeForwardCancelledChat.spigot.chat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.Channels;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

/**
 * Reacts to notifications about cancelled BungeeCord chat events and calls corresponding cancelled
 * {@link AsyncPlayerChatEvent Bukkit chat events}.
 */
public class CancelledChatReceiver implements PluginMessageListener {

	private final Plugin plugin;

	public CancelledChatReceiver(Plugin plugin) {
		assert plugin != null;
		this.plugin = plugin;
	}

	public void onEnable() {
		plugin.getServer().getMessenger().registerIncomingPluginChannel(
				plugin,
				Channels.CANCELLED_CHAT,
				this
		);
	}

	public void onDisable() {
	}

	// Note: This is handled on the server's main thread (not async).
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] data) {
		if (!channel.equals(Channels.CANCELLED_CHAT)) return;

		ByteArrayDataInput input = ByteStreams.newDataInput(data);
		String chatMessage = input.readUTF();
		Log.debug(() -> "Received cancelled chat event: player=" + player.getName()
				+ ", async=" + !Bukkit.isPrimaryThread()
				+ ", message='" + chatMessage + "'");
		this.callCancelledChatEvent(player, chatMessage);
	}

	private void callCancelledChatEvent(Player sender, String message) {
		// Recipients:
		// Empty Set: This chat event is cancelled and not supposed to be sent to any players
		// anyways.
		// New Set: In case any plugins attempt to modify this Set without checking if it even is
		// modifiable.
		Set<Player> recipients = new HashSet<>();

		AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(
				!Bukkit.isPrimaryThread(),
				sender,
				message,
				recipients
		);
		event.setCancelled(true); // Cancelled right away
		Bukkit.getPluginManager().callEvent(event);
		// We will never send a chat message to players for this event, even if some other plugin
		// forcefully uncancels the event.
	}
}
