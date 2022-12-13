package de.blablubbabc.bungeeForwardCancelledChat.bungee.chat.muting;

import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.Channels;
import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.proxybound.MuteNextChat;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * Receives mute requests by Spigot servers.
 */
public class MuteRequestReceiver implements Listener {

	private final Plugin plugin;
	private final ChatMuting chatMuting;

	public MuteRequestReceiver(Plugin plugin, ChatMuting chatMuting) {
		assert plugin != null && chatMuting != null;
		this.plugin = plugin;
		this.chatMuting = chatMuting;
	}

	public void onEnable() {
		plugin.getProxy().registerChannel(Channels.MUTE);
		plugin.getProxy().getPluginManager().registerListener(plugin, this);
	}

	public void onDisable() {
	}

	@EventHandler
	public void onPlugiMessage(PluginMessageEvent event) {
		if (!event.getTag().equals(Channels.MUTE)) return;
		if (event.isCancelled()) return;

		// We are only interested in messages from a server to the proxy:
		if (!(event.getReceiver() instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

		MuteNextChat muteNextChat = MuteNextChat.deserialize(event.getData());
		if (muteNextChat == null) return;

		Log.debug(() -> "Received mute-next request: player=" + player.getName()
				+ ", thread=" + Thread.currentThread().getName());
		chatMuting.setMuteNext(player, true);
	}
}
