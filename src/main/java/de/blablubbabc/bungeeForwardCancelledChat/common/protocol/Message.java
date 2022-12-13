package de.blablubbabc.bungeeForwardCancelledChat.common.protocol;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Abstract base class of all messages.
 */
public abstract class Message {

	protected Message() {
	}

	/**
	 * Serializes the contents of this message, i.e. the data of this message without its type
	 * information.
	 * 
	 * @param out
	 *            the data output to serialize to
	 */
	protected void serializeContent(ByteArrayDataOutput out) {
		// No contents by default.
	}

	/**
	 * Serializes this message.
	 * 
	 * @return the data
	 */
	public final byte[] serialize() {
		MessageType<?> messageType = Protocol.INSTANCE.getMessageType(this.getClass());
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(messageType.getId());
		this.serializeContent(out);
		return out.toByteArray();
	}

	/**
	 * Deserializes a message of unknown type.
	 * 
	 * @param data
	 *            the data
	 * @return the message, not <code>null</code>
	 * @throws RuntimeException
	 *             if the message type is not recognized or if message cannot be deserialized for
	 *             another reason
	 */
	public static Message deserialize(byte[] data) {
		ByteArrayDataInput in = ByteStreams.newDataInput(data);
		String typeId = in.readUTF();
		MessageType<?> messageType = Protocol.INSTANCE.getMessageTypeById(typeId);
		return messageType.deserialize(in);
	}

	/**
	 * Tries to deserialize the given data as a specific type of message.
	 * 
	 * @param <T>
	 *            the message type
	 * @param clazz
	 *            the message class
	 * @param data
	 *            the data
	 * @return the message, or <code>null</code> if the message is of another type
	 * @throws RuntimeException
	 *             if the message cannot be deserialized
	 */
	public static <T extends Message> T deserialize(Class<T> clazz, byte[] data) {
		MessageType<T> messageType = Protocol.INSTANCE.getMessageType(clazz);
		ByteArrayDataInput in = ByteStreams.newDataInput(data);
		String typeId = in.readUTF();
		if (!messageType.getId().equals(typeId)) return null;
		return messageType.deserialize(in);
	}
}
