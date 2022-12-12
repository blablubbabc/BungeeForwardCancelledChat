package de.blablubbabc.bungeeForwardCancelledChat.spigot;

import org.bukkit.configuration.Configuration;

import de.blablubbabc.bungeeForwardCancelledChat.common.debug.Debug;

public class Config {

	private static final String KEY_DEBUG = "debug";

	private final SpigotPlugin plugin;

	public boolean debug = false;

	public Config(SpigotPlugin plugin) {
		assert plugin != null;
		this.plugin = plugin;
	}

	public void saveDefaults() {
		plugin.saveDefaultConfig();
	}

	private void load(Configuration config) {
		debug = config.getBoolean(KEY_DEBUG, debug);
	}

	public void loadAndApply() {
		Configuration configuration = plugin.getConfig();
		this.load(configuration);
		this.apply();
	}

	private void apply() {
		Debug.setDebugging(debug);
	}
}
