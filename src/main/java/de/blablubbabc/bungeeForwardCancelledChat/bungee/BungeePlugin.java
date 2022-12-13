package de.blablubbabc.bungeeForwardCancelledChat.bungee;

import de.blablubbabc.bungeeForwardCancelledChat.bungee.chat.forwarding.CancelledChatForwarder;
import de.blablubbabc.bungeeForwardCancelledChat.bungee.chat.muting.ChatMuting;
import de.blablubbabc.bungeeForwardCancelledChat.bungee.chat.muting.MuteRequestReceiver;
import de.blablubbabc.bungeeForwardCancelledChat.bungee.commands.BFCCCommand;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

	private final Config config = new Config(this);
	private final CancelledChatForwarder cancelledChatForwarder = new CancelledChatForwarder(this);
	private final ChatMuting chatMuting = new ChatMuting(this);
	private final MuteRequestReceiver muteRequestReceiver = new MuteRequestReceiver(this, chatMuting);
	private final BFCCCommand bfccCommand = new BFCCCommand(this, chatMuting);

	@Override
	public void onEnable() {
		Log.setLogger(this.getLogger());

		config.saveDefaults();
		config.loadAndApply();

		cancelledChatForwarder.onEnable();
		chatMuting.onEnable();
		muteRequestReceiver.onEnable();
		bfccCommand.register();
	}

	@Override
	public void onDisable() {
		muteRequestReceiver.onDisable();
		chatMuting.onDisable();
		cancelledChatForwarder.onDisable();
	}
}
