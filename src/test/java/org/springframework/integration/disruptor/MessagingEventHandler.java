package org.springframework.integration.disruptor;

import org.springframework.integration.disruptor.config.annotation.EventHandler;

public class MessagingEventHandler {

	@EventHandler
	public void handler(final MessagingEvent event) {
		System.out.println("Handling: " + event.getPayload());
	}

}
