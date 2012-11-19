package org.springframework.integration.disruptor.config.workflow.translator;

import org.springframework.integration.Message;
import org.springframework.integration.disruptor.MessagingEvent;

public class MessagingEventTranslator implements MessageEventTranslator<MessagingEvent> {

	public void translateTo(final Message<?> message, final MessagingEvent event) {
		event.setPayload(message);
	}

}
