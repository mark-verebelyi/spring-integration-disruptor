package org.springframework.integration.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * {@link EventFactory} that produces {@link MessagingEvent}s.
 */
public final class MessagingEventFactory implements EventFactory<MessagingEvent> {

	public MessagingEvent newInstance() {
		return new MessagingEvent();
	}

}