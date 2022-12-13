package de.blablubbabc.bungeeForwardCancelledChat.spigot.chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.Channels;
import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.proxybound.MuteNextChat;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;
import de.blablubbabc.bungeeForwardCancelledChat.spigot.SpigotPlugin;

public class Muting {

	private Muting() {
	}

	/**
	 * Requests the player's next chat message to be muted on the proxy. See {@link MuteNextChat}.
	 * <p>
	 * This has no effect if the BungeeForwardCancelledChat plugin is currently disabled, or if the
	 * player is no longer online.
	 * 
	 * @param player
	 *            the player, not <code>null</code>
	 */
	public static void muteNextChat(Player player) {
		if (player == null) throw new IllegalArgumentException("player is null");

		if (!player.isOnline()) {
			Log.debug("Ignoring mute-next-chat request: Player is offline: " + player.getName());
			return;
		}

		Plugin plugin = SpigotPlugin.getInstance();
		if (plugin == null) {
			Log.debug("Ignoring mute-next-chat request: Plugin is currently disabled.");
			return;
		}

		MuteNextChat muteNextChat = new MuteNextChat();
		player.sendPluginMessage(plugin, Channels.MUTE, muteNextChat.serialize());
	}
}
