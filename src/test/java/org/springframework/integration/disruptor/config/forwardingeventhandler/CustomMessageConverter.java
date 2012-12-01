package org.springframework.integration.disruptor.config.forwardingeventhandler;

import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.converter.MessageConverter;

public class CustomMessageConverter implements MessageConverter {

	@SuppressWarnings("unchecked")
	public <P> Message<P> toMessage(final Object object) {
		return (Message<P>) MessageBuilder.withPayload(object).setHeader("converted", "header").build();
	}

	public <P> Object fromMessage(final Message<P> message) {
		throw new UnsupportedOperationException("'fromMessage' is not supported in: " + this.getClass());
	}

}
