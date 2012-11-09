package org.springframework.integration.disruptor;

import com.lmax.disruptor.EventFactory;

public class MessagingEventFactory implements EventFactory<MessagingEvent> {

	public MessagingEvent newInstance() {
		return new MessagingEvent();
	}

}