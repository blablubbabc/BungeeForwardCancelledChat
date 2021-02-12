package de.blablubbabc.bungeeForwardCancelledChat.spigot.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.blablubbabc.bungeeForwardCancelledChat.common.Permissions;
import de.blablubbabc.bungeeForwardCancelledChat.common.debug.Debug;

public class BFCCCommand implements CommandExecutor {

	private static final String COMMAND_NAME = "bfcc-s";

	private final JavaPlugin plugin;

	public BFCCCommand(JavaPlugin plugin) {
		assert plugin != null;
		this.plugin = plugin;
	}

	public void register() {
		plugin.getCommand(COMMAND_NAME).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission(Permissions.BFCC_ADMIN)) {
			sender.sendMessage(ChatColor.RED + "You do not have the permission to execute this command.");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Missing arguments. Try /" + COMMAND_NAME + " help");
			return true;
		}

		List<String> argsList = Arrays.asList(args);
		List<String> subCommandArgs = argsList.subList(1, argsList.size());

		String subCommand = args[0].toLowerCase(Locale.ROOT);
		if (subCommand.equals("help") || subCommand.equals("?")) {
			this.sendHelp(sender);
		} else if (subCommand.equals("debug")) {
			this.commandDebug(sender, subCommandArgs);
		} else {
			sender.sendMessage(ChatColor.RED + "Unknown command. Try /" + COMMAND_NAME + " help");
		}
		return true;
	}

	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.BLUE + "---- " + ChatColor.GOLD + plugin.getName()
				+ " (Spigot) v" + plugin.getDescription().getVersion() + ChatColor.BLUE + " ----");
		sender.sendMessage(ChatColor.YELLOW + "/" + COMMAND_NAME + " debug" + ChatColor.DARK_AQUA + " - Toggles debug mode on and off.");
	}

	private void commandDebug(CommandSender sender, List<String> args) {
		Debug.setDebugging(!Debug.isDebugging());
		sender.sendMessage(ChatColor.GREEN + "Turned debug mode " + (Debug.isDebugging() ? "on" : "off"));
	}
}
