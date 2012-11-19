package org.springframework.integration.disruptor.config.workflow.translator;

import org.springframework.integration.Message;

public interface MessageEventTranslator<T> {

	void translateTo(Message<?> message, T event);

}