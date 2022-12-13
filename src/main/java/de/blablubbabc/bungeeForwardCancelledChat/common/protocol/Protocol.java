package de.blablubbabc.bungeeForwardCancelledChat.common.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.common.io.ByteArrayDataInput;

import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.proxybound.MuteNextChat;
import de.blablubbabc.bungeeForwardCancelledChat.common.protocol.serverbound.CancelledChat;

/**
 * Registry of known message types.
 */
public class Protocol {

	/**
	 * The {@link Protocol} instance.
	 */
	public static final Protocol INSTANCE = new Protocol();

	static {
		// Serverbound:
		INSTANCE.register(CancelledChat.class, CancelledChat::deserializeContent);

		// Proxybound:
		INSTANCE.register(MuteNextChat.class, MuteNextChat::deserializeContent);
	}

	private final Map<Class<?>, MessageType<?>> messageTypesByClass = new HashMap<>();
	private final Map<String, MessageType<?>> messageTypesById = new HashMap<>();

	private Protocol() {
	}

	private <T extends Message> void register(
			Class<T> clazz,
			Function<ByteArrayDataInput, T> deserializer
	) {
		MessageType<T> messageType = new MessageType<T>(clazz, deserializer);
		if (messageTypesById.putIfAbsent(messageType.getId(), messageType) != null) {
			throw new IllegalArgumentException("A message type with this id is already registered: "
					+ messageType.getId());
		}
		messageTypesByClass.put(clazz, messageType);
	}

	/**
	 * Gets the {@link MessageType} for the specified class.
	 * 
	 * @param <T>
	 *            the message type
	 * @param clazz
	 *            the message class
	 * @return the message type, not <code>null</code>
	 * @throws RuntimeException
	 *             if the message type is not found
	 */
	@SuppressWarnings("unchecked")
	public <T extends Message> MessageType<T> getMessageType(Class<T> clazz) {
		MessageType<T> messageType = (MessageType<T>) messageTypesByClass.get(clazz);
		if (messageType == null) {
			throw new RuntimeException("Message type not registered: " + clazz.getName());
		}
		return messageType;
	}

	/**
	 * Gets the {@link MessageType} for the specified type id.
	 * 
	 * @param id
	 *            the type id
	 * @return the message type, not <code>null</code>
	 * @throws RuntimeException
	 *             if the message type is not found
	 */
	public MessageType<?> getMessageTypeById(String id) {
		MessageType<?> messageType = messageTypesById.get(id);
		if (messageType == null) {
			throw new RuntimeException("Unknown message type: " + id);
		}
		return messageType;
	}
}
