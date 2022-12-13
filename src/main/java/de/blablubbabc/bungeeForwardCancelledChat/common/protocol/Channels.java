package de.blablubbabc.bungeeForwardCancelledChat.common.protocol;

public class Channels {

	private Channels() {
	}

	private static final String PLUGIN_PREFIX = "bfcc:";

	/**
	 * The channel by which the proxy informs the Spigot servers about cancelled chat messages.
	 */
	public static final String CANCELLED_CHAT = PLUGIN_PREFIX + "cancelled-chat";

	/**
	 * The channel by which the Spigot servers can request a player to be muted on the proxy.
	 */
	public static final String MUTE = PLUGIN_PREFIX + "mute";
}
