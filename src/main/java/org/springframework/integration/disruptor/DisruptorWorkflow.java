package org.springframework.integration.disruptor;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorWorkflow<T> extends AbstractDisruptorWorkflow<T> implements MessageChannel {

	public DisruptorWorkflow(final RingBuffer<T> ringBuffer, final Executor executor, final List<EventProcessor> eventProcessors) {
		super(ringBuffer, executor, eventProcessors);
	}

	public boolean send(final Message<?> message) {
		System.out.println(message);
		return true;
	}

	public boolean send(final Message<?> message, final long timeout) {
		return true;
	}

}
