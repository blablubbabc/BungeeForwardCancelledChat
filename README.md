# BungeeForwardCancelledChat

## About

This is a BungeeCord and Spigot plugin that:
* Forwards cancelled chat events from the BungeeCord proxy to the player's Spigot server.
* Allows plugins on the Spigot servers to request the proxy to cancel the next chat message of a player.

In summary: This plugin may help you resolve certain incompatibilities between BungeeCord chat plugins and Spigot plugins that use chat as a form of player input.

### Problem

Some Spigot plugins use chat inputs as a mechanism to get textual information from players: Think of chatting with NPCs on a role playing server, in-game surveys, or an user interface alternative to other typical means of getting textual input from players (such as commands/anvils/signs/books/etc.).  
For example, the [Shopkeepers](https://www.spigotmc.org/resources/shopkeepers.80756/) plugin, a plugin for creating shop NPCs, allows players to rename their shop NPC by typing the new name into chat.

There are some problems that can arise when the Spigot server is behind a BungeeCord (or other kind of) proxy:
* These Spigot plugins rely on being able to detect chat inputs by players (i.e. chat events). On a normal Spigot server, detecting these chat events is not an issue. However, behind a proxy, some plugins on the proxy may prevent chat packets from reaching the player's underlying Spigot server. Typical examples are chat muting plugins, or chat plugins that cancel the chat event on the proxy and then manually send chat messages to all players on the network.  
  When the chat event is cancelled on the proxy, the player's chat packet is not forwarded to their current Spigot server, and will consequently not result in a chat event that other plugins can detect there. In the Shopkeepers example given above, this would result in players no longer being able to rename their shop NPCs.
* When using chat as textual input, the plugins on the Spigot server will usually cancel the player's chat event in order to hide the message from other players. But chat plugins on the proxy usually do not take into account whether a plugin on the Spigot server wants to cancel the chat event. Consequently, if you use a chat plugin on the proxy that forwards chat messages to other players in the network, these players will still receive the chat messages that were meant to be cancelled.

### Solution

* This plugin forwards cancelled chat events from the proxy to the player's Spigot server, where it calls a surrogate cancelled AsyncPlayerChatEvent that other Spigot plugins can then react to.  
  The intend of calling Bukkit's AsyncPlayerChatEvent instead of a custom event is that these other plugins should not have to adapt to whether or not they are running behind a BungeeCord proxy: They can simply react to the normal chat event as usual.  
  **Note:** The chat event called on the Spigot server is cancelled right away. If this results in chat messages being sent to players on your server, you are probably using a chat plugin that ignores this cancellation state. If this is the case, there is nothing I can do about that. This is something for this chat plugin to fix then.
* In order to resolve the second issue mentioned above, this plugin enables other Spigot plugins to request the cancellation of the next incoming chat message of a player on the proxy.  
  This is done by sending the plugin message `MuteNextChat` in the channel `bfcc:mute`.  
  Alternatively, you can add this plugin as a dependency and call `Muting.muteNextChat(Player)`.  
  The cancellation request is only valid for a single chat message, and only as long as the player is still connected to the same server.

## Open issues

On MC 1.19.x, with the signed chat feature added, canceling individual chat messages on the proxy, e.g. due to the player being muted temporarily, can result in the player to be kicked once they are no send their next message after they are no longer muted. This also affects the second part of the solution mentioned above.

## Other considered solutions

* Forward chat events early from the BungeeCord proxy to the player's Spigot server and return its outcome there back to the proxy: Depending on whether the chat event is cancelled on the Spigot server, the chat event is then either also cancelled or further processed on the proxy. However, there are some difficulties with this approach:
  * The chat processing needs to be paused on the proxy and resumed once the result comes in, for each chat message. This introduces additional latency in the chat processing.
  * A fallback mechanism, such as a timeout, is required to account for cases in which we receive no response from the Spigot server.
  * With the chat signing introduced in MC 1.19, it has become more tricky to replay chat events via the BungeeCord API.
  * Chat plugins on the proxy, or even the proxy itself, might forward the message again to the player's Spigot server, resulting in duplicate message processing there.
  * The player might quit or change their server during the chat processing.

## Installation

Copy the plugin jar to both the BungeeCord's and all of your Spigot servers' plugin folders.

## Commands

### BungeeCord commands

* `/bfcc debug` (permission `bfcc.admin`): Toggles the debugging state on the BungeeCord side (e.g. logs when cancelled chat events are forwarded to the Spigot server).
* `/bfcc mute` (permission `bfcc.admin`): Mutes and unmutes yourself. This command is meant for testing purposes, to verify that this plugin works as expected.

In order to use these commands you will have to add the `bfcc.admin` permission to your admin group in your BungeeCord config.

### Spigot commands

* `/bfcc-s debug` (permission `bfcc.admin`, default `op`): Toggles the debugging state on the Spigot server side (e.g. logs when cancelled chat events are received on the Spigot server).

## Further references

**Spigot plugin page:** https://www.spigotmc.org/resources/bungeeforwardcancelledchat.88941/
