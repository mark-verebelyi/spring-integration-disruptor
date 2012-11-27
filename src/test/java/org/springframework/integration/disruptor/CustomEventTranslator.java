package org.springframework.integration.disruptor;

import org.springframework.integration.Message;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;

public class CustomEventTranslator implements MessageEventTranslator<CustomEvent> {

	public void translateTo(final Message<?> message, final CustomEvent event) {
		event.setObject(message.getPayload());
	}

}
