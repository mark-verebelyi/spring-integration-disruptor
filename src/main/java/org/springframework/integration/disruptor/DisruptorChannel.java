package org.springframework.integration.disruptor;

import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.dispatcher.MessageDispatcher;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

public class DisruptorChannel extends AbstractSubscribableChannel {

	private final MessageDispatcher dispatcher;

	public DisruptorChannel() {
		this.dispatcher = new DisruptorDispatcher();
	}

	public DisruptorChannel(final ClaimStrategy claimStrategy, final WaitStrategy waitStrategy) {
		this.dispatcher = new DisruptorDispatcher(claimStrategy, waitStrategy);
	}

	@Override
	protected MessageDispatcher getDispatcher() {
		return this.dispatcher;
	}

}
