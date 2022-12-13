package de.blablubbabc.bungeeForwardCancelledChat.common.protocol.serverbound;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.Message;

/**
 * Informs a server about a player's chat message that has been cancelled on the proxy.
 */
public class CancelledChat extends Message {

	private final String message;

	public CancelledChat(String message) {
		this.message = message;
	}

	/**
	 * Gets the chat message.
	 * 
	 * @return the chat message
	 */
	public String getMessage() {
		return message;
	}

	@Override
	protected void serializeContent(ByteArrayDataOutput out) {
		out.writeUTF(message);
	}

	public static CancelledChat deserializeContent(ByteArrayDataInput in) {
		String message = in.readUTF();
		return new CancelledChat(message);
	}

	/**
	 * Tries to deserialize the given data as a {@link CancelledChat} message.
	 * 
	 * @param data
	 *            the data
	 * @return the message, or <code>null</code> if the message is of another type
	 * @throws RuntimeException
	 *             if the message cannot be deserialized
	 */
	public static CancelledChat deserialize(byte[] data) {
		return Message.deserialize(CancelledChat.class, data);
	}
}
