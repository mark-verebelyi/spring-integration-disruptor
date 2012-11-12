package org.springframework.integration.disruptor.config;

final class DisruptorNamespaceElements {

	private DisruptorNamespaceElements() {
		throw new IllegalStateException("Utility class, do not instantiate");
	}

	public static final String RING_BUFFER_ATTRIBUTE_EVENT_FACTORY = "event-factory";
	public static final String RING_BUFFER_ATTRIBUTE_CLAIM_STRATEGY = "claim-strategy";
	public static final String RING_BUFFER_ATTRIBUTE_WAIT_STRATEGY = "wait-strategy";
	public static final String RING_BUFFER_ATTRIBUTE_SIZE = "buffer-size";

	public static final String DISRUPTOR_ATTRIBUTE_EXECUTOR = "executor";

	public static final String CHANNEL_ATTRIBUTE_DISRUPTOR = "disruptor";

}