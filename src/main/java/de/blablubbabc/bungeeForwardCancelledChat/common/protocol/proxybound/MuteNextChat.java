package de.blablubbabc.bungeeForwardCancelledChat.common.protocol.proxybound;

import com.google.common.io.ByteArrayDataInput;

import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.Message;

/**
 * Request to mute the player's next chat message.
 * <p>
 * The request is only valid for a single chat message, and only while the player is still connected
 * to the same server.
 * <p>
 * The cancelled chat message will still be forwarded to the player's server.
 */
public class MuteNextChat extends Message {

	public MuteNextChat() {
	}

	public static MuteNextChat deserializeContent(ByteArrayDataInput in) {
		return new MuteNextChat();
	}

	/**
	 * Tries to deserialize the given data as a {@link MuteNextChat} message.
	 * 
	 * @param data
	 *            the data
	 * @return the message, or <code>null</code> if the message is of another type
	 * @throws RuntimeException
	 *             if the message cannot be deserialized
	 */
	public static MuteNextChat deserialize(byte[] data) {
		return Message.deserialize(MuteNextChat.class, data);
	}
}
