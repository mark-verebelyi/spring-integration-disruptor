package org.springframework.integration.disruptor;

import org.springframework.integration.Message;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;

/**
 * Holder class for {@link Message}s in the {@link RingBuffer}.
 * 
 * This is the default event type for the {@link MessageDrivenDisruptorWorkflow}.
 */
public final class MessagingEvent {

	private volatile Message<?> payload;

	public Message<?> getPayload() {
		return this.payload;
	}

	public void setPayload(final Message<?> payload) {
		this.payload = payload;
	}

	public static EventFactory<MessagingEvent> newEventFactory() {
		return new MessagingEventFactory();
	}

}