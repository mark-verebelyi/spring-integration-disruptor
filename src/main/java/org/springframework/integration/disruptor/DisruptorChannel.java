package org.springframework.integration.disruptor;

import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.dispatcher.MessageDispatcher;

public class DisruptorChannel extends AbstractSubscribableChannel {

	private final MessageDispatcher dispatcher;

	public DisruptorChannel() {
		this.dispatcher = new DisruptorDispatcher();
	}

	@Override
	protected MessageDispatcher getDispatcher() {
		return this.dispatcher;
	}

}
