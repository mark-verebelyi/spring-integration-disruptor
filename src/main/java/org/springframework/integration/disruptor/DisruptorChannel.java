package org.springframework.integration.disruptor;

import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.dispatcher.MessageDispatcher;

import com.lmax.disruptor.WaitStrategy;

public class DisruptorChannel extends AbstractSubscribableChannel {

	private final MessageDispatcher dispatcher;

	public DisruptorChannel(final int ringBufferSize, final WaitStrategy waitStrategy) {
		this.dispatcher = new DisruptorDispatcher(ringBufferSize, waitStrategy);
	}

	@Override
	protected MessageDispatcher getDispatcher() {
		return this.dispatcher;
	}

}
