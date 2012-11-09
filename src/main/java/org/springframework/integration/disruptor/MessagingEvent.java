package org.springframework.integration.disruptor;

import org.springframework.integration.Message;

import com.lmax.disruptor.EventFactory;

public final class MessagingEvent {

	private Message<?> payload;

	public Message<?> getPayload() {
		return this.payload;
	}

	public void setPayload(final Message<?> payload) {
		this.payload = payload;
	}

	public static EventFactory<MessagingEvent> newEventFactory() {
		return new EventFactory<MessagingEvent>() {

			public MessagingEvent newInstance() {
				return new MessagingEvent();
			}

		};
	}

}