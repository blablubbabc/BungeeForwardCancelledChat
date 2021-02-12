package de.blablubbabc.bungeeForwardCancelledChat.bungee.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.blablubbabc.bungeeForwardCancelledChat.bungee.ChatHandler;
import de.blablubbabc.bungeeForwardCancelledChat.common.Permissions;
import de.blablubbabc.bungeeForwardCancelledChat.common.debug.Debug;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class BFCCCommand extends Command {

	private static BaseComponent[] legacy(String legacyText) {
		return TextComponent.fromLegacyText(legacyText);
	}

	private final Plugin plugin;
	private final ChatHandler chatHandler;

	public BFCCCommand(Plugin plugin, ChatHandler chatHandler) {
		super("bfcc", null, "BungeeForwardCancelledChat");
		assert plugin != null && chatHandler != null;
		this.plugin = plugin;
		this.chatHandler = chatHandler;
	}

	public void register() {
		plugin.getProxy().getPluginManager().registerCommand(plugin, this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permissions.BFCC_ADMIN)) {
			sender.sendMessage(legacy(ChatColor.RED + "You do not have the permission to execute this command."));
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(legacy(ChatColor.RED + "Missing arguments. Try /bfcc help"));
			return;
		}

		List<String> argsList = Arrays.asList(args);
		List<String> subCommandArgs = argsList.subList(1, argsList.size());

		String subCommand = args[0].toLowerCase(Locale.ROOT);
		if (subCommand.equals("help") || subCommand.equals("?")) {
			this.sendHelp(sender);
		} else if (subCommand.equals("debug")) {
			this.commandDebug(sender, subCommandArgs);
		} else if (subCommand.equals("mute")) {
			this.commandMute(sender, subCommandArgs);
		} else {
			sender.sendMessage(legacy(ChatColor.RED + "Unknown command. Try /bfcc help"));
		}
	}

	private void sendHelp(CommandSender sender) {
		sender.sendMessage(legacy(ChatColor.BLUE + "---- " + ChatColor.GOLD + plugin.getDescription().getName()
				+ " (Bungee) v" + plugin.getDescription().getVersion() + ChatColor.BLUE + " ----"));
		sender.sendMessage(legacy(ChatColor.YELLOW + "/bfcc debug" + ChatColor.DARK_AQUA + " - Toggles debug mode on and off."));
		sender.sendMessage(legacy(ChatColor.YELLOW + "/bfcc mute" + ChatColor.DARK_AQUA + " - Mutes and unmutes yourself."));
	}

	private void commandDebug(CommandSender sender, List<String> args) {
		Debug.setDebugging(!Debug.isDebugging());
		sender.sendMessage(legacy(ChatColor.GREEN + "Turned debug mode " + (Debug.isDebugging() ? "on" : "off")));
	}

	private void commandMute(CommandSender sender, List<String> args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(legacy(ChatColor.RED + "This command can only be executed as player!"));
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		boolean newMuted = !chatHandler.isMuted(player);
		chatHandler.setMuted(player, newMuted);
		sender.sendMessage(legacy(ChatColor.GREEN + "You are " + (newMuted ? "now muted" : "no longer muted")));
	}
}
