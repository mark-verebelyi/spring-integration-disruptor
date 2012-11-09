package org.springframework.integration.disruptor;

import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.dispatcher.MessageDispatcher;

import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorChannel<T> extends AbstractSubscribableChannel {

	private final MessageDispatcher dispatcher;

	public DisruptorChannel() {
		this.dispatcher = new DisruptorDispatcher();
	}

	public DisruptorChannel(final Disruptor<T> disruptor) {
		this.dispatcher = new DisruptorDispatcher(claimStrategy, waitStrategy);
	}

	@Override
	protected MessageDispatcher getDispatcher() {
		return this.dispatcher;
	}

}
