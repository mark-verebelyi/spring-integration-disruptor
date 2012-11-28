package org.springframework.integration.disruptor;

import java.util.List;
import java.util.concurrent.Executor;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorWorkflow<T> extends AbstractDisruptorWorkflow<T> {

	public DisruptorWorkflow(final RingBuffer<T> ringBuffer, final Executor executor, final List<EventProcessor> eventProcessors) {
		super(ringBuffer, executor, eventProcessors);
	}

	@Override
	protected void doStart() {
	}

	@Override
	protected void doStop() {
	}

}
