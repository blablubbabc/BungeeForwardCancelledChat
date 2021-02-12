package de.blablubbabc.bungeeForwardCancelledChat.bungee;

import de.blablubbabc.bungeeForwardCancelledChat.bungee.commands.BFCCCommand;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

	private final ChatHandler chatHandler = new ChatHandler(this);
	private final BFCCCommand bfccCommand = new BFCCCommand(this, chatHandler);

	@Override
	public void onEnable() {
		Log.setLogger(this.getLogger());
		chatHandler.onEnable();
		bfccCommand.register();
	}

	@Override
	public void onDisable() {
		chatHandler.onDisable();
	}
}
