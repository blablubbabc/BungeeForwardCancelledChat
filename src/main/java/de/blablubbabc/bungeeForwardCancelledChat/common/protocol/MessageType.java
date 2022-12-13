package de.blablubbabc.bungeeForwardCancelledChat.common.protocol;

import java.util.function.Function;

import com.google.common.io.ByteArrayDataInput;

/**
 * Information about a type of message.
 *
 * @param <T>
 *            the message type
 */
public final class MessageType<T extends Message> {

	private final String id;
	private final Function<ByteArrayDataInput, T> deserializer;

	MessageType(Class<T> clazz, Function<ByteArrayDataInput, T> deserializer) {
		if (clazz == null) throw new IllegalArgumentException("clazz is null");
		if (deserializer == null) throw new IllegalArgumentException("deserializer is null");

		this.id = clazz.getSimpleName();
		if (id.isEmpty()) {
			throw new IllegalArgumentException("Message type cannot be anonymous! " + clazz.getName());
		}
		this.deserializer = deserializer;
	}

	/**
	 * Gets the id that uniquely identifies this message type.
	 * 
	 * @return the id, not <code>null</code> or empty
	 */
	public String getId() {
		return id;
	}

	/**
	 * Reconstructs the message from the given content data (i.e. without the message header).
	 * 
	 * @param in
	 *            the content data
	 * @return the message, not <code>null</code>
	 * @throws RuntimeException
	 *             if the deserialization fails
	 */
	public T deserialize(ByteArrayDataInput in) {
		try {
			T message = deserializer.apply(in);
			if (message == null) {
				throw new RuntimeException("Deserializer returned null!");
			}
			return message;
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize message of type " + id, e);
		}
	}
}
