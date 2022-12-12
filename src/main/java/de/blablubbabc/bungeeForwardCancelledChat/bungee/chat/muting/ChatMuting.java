package de.blablubbabc.bungeeForwardCancelledChat.bungee.chat.muting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Handles the muting of chat for debugging and testing purposes.
 */
public class ChatMuting implements Listener {

	private final Set<UUID> mutedPlayers = new HashSet<>();

	public ChatMuting() {
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatEarly(ChatEvent event) {
		// Ignore if already cancelled:
		if (event.isCancelled()) return;

		// Ignore commands:
		if (event.isCommand()) return;

		// Ignore if the sender is not a player:
		if (!(event.getSender() instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// Mute the player:
		if (this.isMuted(player)) {
			Log.debug(() -> "Cancelling chat message of muted player " + player.getName());
			event.setCancelled(true);
		}
	}
}
