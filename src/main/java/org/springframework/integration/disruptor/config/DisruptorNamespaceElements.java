package org.springframework.integration.disruptor.config;

final class DisruptorNamespaceElements {

	private DisruptorNamespaceElements() {
		throw new IllegalStateException("Utility class, do not instantiate");
	}

	public static final String ELEMENT_DISRUPTOR = "disruptor";
	public static final String ELEMENT_RING_BUFFER = "ring-buffer";
	public static final String ELEMENT_CHANNEL = "channel";
	public static final String ELEMENT_MESSAGING_EVENT_FACTORY = "messaging-event-factory";
	public static final String ELEMENT_MESSAGE_DRIVEN_WORKFLOW = "message-driven-workflow";
	public static final String ELEMENT_WORKFLOW = "workflow";
	public static final String ELEMENT_FORWARDING_EVENT_HANDLER = "forwarding-event-handler";

	public static final String RING_BUFFER_ATTRIBUTE_EVENT_FACTORY = "event-factory";
	public static final String RING_BUFFER_ATTRIBUTE_CLAIM_STRATEGY = "claim-strategy";
	public static final String RING_BUFFER_ATTRIBUTE_WAIT_STRATEGY = "wait-strategy";
	public static final String RING_BUFFER_ATTRIBUTE_BUFFER_SIZE = "buffer-size";

	public static final String DISRUPTOR_ATTRIBUTE_EXECUTOR = "executor";

	public static final String CHANNEL_ATTRIBUTE_DISRUPTOR = "disruptor";

}