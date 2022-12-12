package de.blablubbabc.bungeeForwardCancelledChat.bungee;

import java.io.File;
import java.io.IOException;

import de.blablubbabc.bungeeForwardCancelledChat.common.debug.Debug;
import de.blablubbabc.bungeeForwardCancelledChat.common.util.Log;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Config {

	private static final String FILE_NAME = "config.yml";
	private static final String KEY_DEBUG = "debug";

	private final BungeePlugin plugin;

	public boolean debug = false;

	public Config(BungeePlugin plugin) {
		assert plugin != null;
		this.plugin = plugin;
	}

	private File getFile() {
		return new File(plugin.getDataFolder(), FILE_NAME);
	}

	public void saveDefaults() {
		File configFile = this.getFile();
		if (configFile.exists()) return;

		Configuration configuration = new Configuration();
		Config defaults = new Config(plugin);
		defaults.save(configuration);

		try {
			configFile.getParentFile().mkdirs();
			ConfigurationProvider.getProvider(YamlConfiguration.class)
					.save(configuration, configFile);
		} catch (IOException e) {
			Log.severe("Failed to save default config!", e);
		}
	}

	private void save(Configuration config) {
		config.set(KEY_DEBUG, debug);
	}

	private void load(Configuration config) {
		debug = config.getBoolean(KEY_DEBUG, debug);
	}

	private void load(File configFile) {
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(configFile);
			if (configuration != null) {
				this.load(configuration);
			}
		} catch (IOException e) {
			Log.severe("Failed to load config!", e);
		}
	}

	public void loadAndApply() {
		File configFile = this.getFile();
		this.load(configFile);
		this.apply();
	}

	private void apply() {
		Debug.setDebugging(debug);
	}
}
