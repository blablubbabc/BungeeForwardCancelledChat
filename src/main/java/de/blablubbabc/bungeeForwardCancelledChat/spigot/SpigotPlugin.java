package de.blablubbabc.bungeeForwardCancelledChat.spigot;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;
import de.blablubbabc.bungeeForwardCancelledChat.spigot.chat.CancelledChatReceiver;
import de.blablubbabc.bungeeForwardCancelledChat.spigot.commands.BFCCCommand;

public class SpigotPlugin extends JavaPlugin {

	private final CancelledChatReceiver cancelledChatReceiver = new CancelledChatReceiver(this);
	private final BFCCCommand bfccCommand = new BFCCCommand(this);

	@Override
	public void onEnable() {
		Log.setLogger(this.getLogger());
		if (!this.checkIfSpigot()) {
			this.getLogger().severe("This server is not using Spigot! Disabling ...");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (!this.checkIfBungeeCord()) {
			this.getLogger().severe("This server is not using BungeeCord mode!");
			this.getLogger().severe("If you are using BungeeCord for this server, please also "
					+ "enable 'settings.bungeecord' inside your spigot.yml config.");
			this.getLogger().severe("Disabling ...");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		cancelledChatReceiver.onEnable();
		bfccCommand.register();
	}

	// Returns true if the server is running Spigot (instead of for example CraftBukkit):
	private boolean checkIfSpigot() {
		try {
			Class.forName("org.bukkit.Server$Spigot");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	// Returns true if BungeeCord is enabled:
	private boolean checkIfBungeeCord() {
		Configuration spigotConfig = this.getServer().spigot().getConfig();
		return spigotConfig.getBoolean("settings.bungeecord");
	}

	@Override
	public void onDisable() {
		cancelledChatReceiver.onDisable();
	}
}
