package org.springframework.integration.disruptor;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorWorkflow<T> extends AbstractDisruptorWorkflow<T> implements MessageChannel {

	public DisruptorWorkflow(final RingBuffer<T> ringBuffer, final Executor executor, final List<EventProcessor> eventProcessors,
			final MessageEventTranslator<T> messageEventTranslator) {
		super(ringBuffer, executor, eventProcessors, messageEventTranslator);
	}

	public boolean send(final Message<?> message) {
		return this.publish(message);
	}

	public boolean send(final Message<?> message, final long timeout) {
		this.logger.warn("Timeout is ignored in DisruptorWorkflow.");
		return this.send(message);
	}
}
