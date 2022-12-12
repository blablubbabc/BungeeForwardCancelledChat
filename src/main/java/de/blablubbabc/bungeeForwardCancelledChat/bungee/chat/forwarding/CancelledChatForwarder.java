package de.blablubbabc.bungeeForwardCancelledChat.bungee.chat.forwarding;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.Channels;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * Forwards cancelled chat events to the player's server.
 */
public class CancelledChatForwarder implements Listener {

	private final Plugin plugin;

	public CancelledChatForwarder(Plugin plugin) {
		assert plugin != null;
		this.plugin = plugin;
	}

	public void onEnable() {
		plugin.getProxy().registerChannel(Channels.CANCELLED_CHAT);
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	public void onDisable() {
	}

	// Forward cancelled chat events to the player's Spigot server.
	// Priority: As late as possible since we only want to react to cancelled events.
	@EventHandler(priority = Byte.MAX_VALUE)
	public void onChatLate(ChatEvent event) {
		// We only forward cancelled events, since non-cancelled events are already forwarded by
		// BungeeCord itself:
		if (!event.isCancelled()) return;

		// Ignore if the message is empty:
		String message = event.getMessage();
		if (message == null || message.isEmpty()) return;

		// Ignore if this is a command:
		if (event.isCommand()) return;

		// Ignore if the sender is not a player:
		if (!(event.getSender() instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// Verify that the player is still connected:
		if (!player.isConnected()) return;

		// Forward the cancelled chat event:
		this.forwardCancelledChatEvent(player, event);
	}

	private void forwardCancelledChatEvent(ProxiedPlayer player, ChatEvent event) {
		assert player != null && player.isConnected();
		assert event != null;

		String message = event.getMessage();

		Log.debug(() -> "Forwarding cancelled chat event: player=" + player.getName()
				+ ", message='" + message + "'");

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(message);
		player.getServer().sendData(Channels.CANCELLED_CHAT, out.toByteArray());
	}
}
