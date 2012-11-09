package org.springframework.integration.disruptor;

import org.springframework.integration.channel.AbstractSubscribableChannel;
import org.springframework.integration.dispatcher.MessageDispatcher;

import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorChannel extends AbstractSubscribableChannel {

	private final DisruptorDispatcher dispatcher;

	public DisruptorChannel(final Disruptor<MessagingEvent> disruptor) {
		this.dispatcher = new DisruptorDispatcher(disruptor);
	}

	@Override
	protected MessageDispatcher getDispatcher() {
		return this.dispatcher;
	}

	@Override
	protected void onInit() throws Exception {
		this.dispatcher.onInit();
	}

}
