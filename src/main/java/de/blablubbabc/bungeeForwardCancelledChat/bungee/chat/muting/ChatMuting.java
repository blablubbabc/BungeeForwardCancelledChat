package de.blablubbabc.bungeeForwardCancelledChat.bungee.chat.muting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.blablubbabc.bungeeForwardCancelledChat.bungee.BungeePlugin;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Handles the muting of chat, either for debugging and testing purposes, or when a plugin requests
 * the next chat message of a player to be muted because it is interpreted as text input.
 */
public class ChatMuting implements Listener {

	private final BungeePlugin plugin;
	// TODO Do we need to make this thread-safe?
	private final Set<UUID> mutedPlayers = new HashSet<>();
	private final Set<UUID> muteNextPlayers = new HashSet<>();

	public ChatMuting(BungeePlugin plugin) {
		assert plugin != null;
		this.plugin = plugin;
	}

	public void onEnable() {
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	public void onDisable() {
	}

	public void setMuted(ProxiedPlayer player, boolean muted) {
		if (muted) {
			mutedPlayers.add(player.getUniqueId());
		} else {
			mutedPlayers.remove(player.getUniqueId());
		}
	}

	public boolean isMuted(ProxiedPlayer player) {
		return mutedPlayers.contains(player.getUniqueId());
	}

	public void setMuteNext(ProxiedPlayer player, boolean muted) {
		if (muted) {
			muteNextPlayers.add(player.getUniqueId());
		} else {
			muteNextPlayers.remove(player.getUniqueId());
		}
	}

	public boolean isMuteNext(ProxiedPlayer player) {
		return muteNextPlayers.contains(player.getUniqueId());
	}

	// Returns true if the player's next chat message was muted.
	private boolean checkAndResetMuteNext(ProxiedPlayer player) {
		return muteNextPlayers.remove(player.getUniqueId());
	}

	private void resetMuteNextOnServerDisconnect(ProxiedPlayer player) {
		if (this.checkAndResetMuteNext(player)) {
			Log.debug(() -> "Reset one-time mute of disconnected player " + player.getName());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatEarly(ChatEvent event) {
		// Ignore if already cancelled:
		if (event.isCancelled()) return;

		// Ignore commands:
		if (event.isCommand()) return;

		// Ignore if the sender is not a player:
		if (!(event.getSender() instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// Handle one-time mute requests:
		if (this.checkAndResetMuteNext(player)) {
			Log.debug(() -> "Cancelling chat message of one-time muted player " + player.getName());
			event.setCancelled(true);
			return;
		}

		// Handle general muting:
		if (this.isMuted(player)) {
			Log.debug(() -> "Cancelling chat message of muted player " + player.getName());
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onServerDisconnect(ServerDisconnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		this.resetMuteNextOnServerDisconnect(player);
	}
}
