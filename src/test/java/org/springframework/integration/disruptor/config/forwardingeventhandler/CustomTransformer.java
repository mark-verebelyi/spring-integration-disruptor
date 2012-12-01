package org.springframework.integration.disruptor.config.forwardingeventhandler;

import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;

public class CustomTransformer implements Transformer {

	public Message<?> transform(final Message<?> message) {
		return MessageBuilder.fromMessage(message).setHeader("transformed", "header").build();
	}

}
