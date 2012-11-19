package org.springframework.integration.disruptor;

import org.springframework.integration.Message;

interface MessageToEventTranslator<T> {

	void translateTo(Message<?> message, T event);

}