# BungeeForwardCancelledChat

## About

This is a BungeeCoord and Spigot plugin that forwards cancelled chat events from the BungeeCoord proxy to the player's Spigot server.

Some Spigot plugins use chat inputs as a mechanism to get textual information from players: Think of chatting with NPCs on a role playing server, in-game surveys, or an user interface alternative to other typical means of getting textual input from players (such as commands/anvils/signs/books/etc.).  
For example, the [Shopkeepers](https://www.spigotmc.org/resources/shopkeepers.80756/) plugin, a plugin for creating shop NPCs, allows players to rename their shop NPC by typing the new name into chat.

These plugins rely on being able to detect chat inputs by players (i.e. chat events). On a regular Spigot server, detecting these chat events is not an issue. However, if a BungeeCoord (or other kind of) proxy is used, some plugins on the proxy may prevent chat packets from reaching the player's underlying Spigot server. Typical examples are chat muting plugins, or chat plugins that cancel the chat event on the proxy and then manually send chat messages to all players on the network.

When the chat event is cancelled on the proxy, the player's chat packet is not forwarded to the player's current Spigot server, and will consequently not result in a chat event that other plugins can detect there. In the Shopkeeper example given above, this would result in players no longer being able to rename their shop NPC.

This plugin simply forwards these cancelled chat events to the player's Spigot server and calls a surrogate AsyncPlayerChatEvent that other Spigot plugins can then react to. The intend of calling Bukkit's AsyncPlayerChatEvent instead of a custom event is that these other plugins should not have to adapt to whether or not they are running behind a BungeeCoord proxy: They can simply react to the regular chat event as usual.  
The called chat event is cancelled right away and should not result in any chat messages being sent to players (this assumes that no plugin on the Spigot server ignores the cancellation state and then manually sends chat messages to players).

**Summary:** This plugin may help you resolve certain incompatibilities between Spigot plugins that rely on being able to detect chat events, and BungeeCoord chat plugins.

## Installation

Copy the plugin jar to both the BungeeCoord's and all of your Spigot servers' plugin folders.

## Commands

### BungeeCoord commands

* `/bfcc debug` (permission `bfcc.admin`): Toggles the debugging state on the BungeeCoord side (logs when cancelled chat events are forwarded to the Spigot server).
* `/bfcc mute` (permission `bfcc.admin`): Mutes and unmutes yourself. This command is meant for testing purposes, to verify that this plugin works as expected.

In order to use these commands you will have to add the `bfcc.admin` permission to your admin group in your BungeeCoord config.

### Spigot commands

* `/bfcc-s debug` (permission `bfcc.admin`, default `op`): Toggles the debugging state on the Spigot server side (logs when cancelled chat events are received on the Spigot server).

## Further references

**Spigot plugin page:** https://www.spigotmc.org/resources/bungeeforwardcancelledchat.88941/
