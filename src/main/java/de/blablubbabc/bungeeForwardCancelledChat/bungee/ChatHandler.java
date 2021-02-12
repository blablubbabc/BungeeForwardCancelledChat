package de.blablubbabc.bungeeForwardCancelledChat.bungee;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.blablubbabc.bungeeForwardCancelledChat.common.Channels;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Forwards cancelled chat events to the player's server.
 */
public class ChatHandler implements Listener {

	private final Plugin plugin;
	private final Set<UUID> mutedPlayers = new HashSet<>();

	public ChatHandler(Plugin plugin) {
		assert plugin != null;
		this.plugin = plugin;
	}

	public void onEnable() {
		plugin.getProxy().registerChannel(Channels.CANCELLED_CHAT);
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	public void onDisable() {
	}

	public boolean isMuted(ProxiedPlayer player) {
		return mutedPlayers.contains(player.getUniqueId());
	}

	public void setMuted(ProxiedPlayer player, boolean muted) {
		if (muted) {
			mutedPlayers.add(player.getUniqueId());
		} else {
			mutedPlayers.remove(player.getUniqueId());
		}
	}

	// Handles the muting of players.
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatEarly(ChatEvent event) {
		// Ignore if already cancelled:
		if (event.isCancelled()) {
			return;
		}

		// Ignore commands:
		if (event.isCommand()) {
			return;
		}

		// Verify that the sender is a player (else ignore):
		if (!(event.getSender() instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// Handle muting:
		if (this.isMuted(player)) {
			Log.debug(() -> "Cancelling chat message of muted player " + player.getName());
			event.setCancelled(true);
		}
	}

	// Forwards cancelled chat events.
	// Priority: As late as possible since we only want to react to cancelled events.
	@EventHandler(priority = Byte.MAX_VALUE)
	public void onChatLate(ChatEvent event) {
		// We only forward cancelled events, since non-cancelled events are already getting forwarded by BungeeCoord
		// itself:
		if (!event.isCancelled()) {
			return;
		}

		// Ignore if this is a command:
		if (event.isCommand()) {
			return;
		}

		// Verify that the sender is a player (else ignore):
		if (!(event.getSender() instanceof ProxiedPlayer)) {
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// Check if the player is still connected (an other plugin might have kicked the player):
		if (!player.isConnected()) {
			return;
		}

		// Ignore if the message is empty:
		String message = event.getMessage();
		if (message == null || message.isEmpty()) {
			return;
		}

		// Forward the cancelled chat event:
		Log.debug(() -> "Forwarding cancelled chat event: player=" + player.getName() + ", message='" + message + "'");
		this.forwardCancelledChatEvent(player, message);
	}

	private void forwardCancelledChatEvent(ProxiedPlayer player, String chatMessage) {
		assert player != null && player.isConnected();
		assert chatMessage != null && !chatMessage.isEmpty();
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(chatMessage);
		player.getServer().sendData(Channels.CANCELLED_CHAT, out.toByteArray());
	}
}
