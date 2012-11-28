package org.springframework.integration.disruptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.util.Assert;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.EventPublisher;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;

public final class MessageDrivenDisruptorWorkflow<T> extends AbstractDisruptorWorkflow<T> implements MessageHandler, SmartLifecycle {

	private final MessageEventTranslator<T> messageEventTranslator;
	private final List<EventDrivenConsumer> eventDrivenConsumers;

	public MessageDrivenDisruptorWorkflow(final RingBuffer<T> ringBuffer, final Executor executor, final List<EventProcessor> eventProcessors,
			final MessageEventTranslator<T> messageEventTranslator, final List<SubscribableChannel> subscribableChannels) {
		super(ringBuffer, executor, eventProcessors);
		Assert.isTrue(messageEventTranslator != null, "MessageEventTranslator can not be null");
		Assert.isTrue(subscribableChannels != null, "SubscribableChannels can not be null");
		this.messageEventTranslator = messageEventTranslator;
		this.eventDrivenConsumers = toEventDrivenConsumers(subscribableChannels, this);
	}

	private static List<EventDrivenConsumer> toEventDrivenConsumers(final List<SubscribableChannel> subscribableChannels, final MessageHandler messageHandler) {
		final List<EventDrivenConsumer> eventDrivenConsumers = new ArrayList<EventDrivenConsumer>();
		for (final SubscribableChannel subscribableChannel : subscribableChannels) {
			eventDrivenConsumers.add(new EventDrivenConsumer(subscribableChannel, messageHandler));
		}
		return eventDrivenConsumers;
	}

	public void handleMessage(final Message<?> message) throws MessagingException {
		new EventPublisher<T>(this.getRingBuffer()).publishEvent(new EventTranslator<T>() {

			public void translateTo(final T event, final long sequence) {
				MessageDrivenDisruptorWorkflow.this.messageEventTranslator.translateTo(message, event);
			}

		});
	}

	@Override
	public void doStart() {
		this.startEventDrivenConsumers();
	}

	@Override
	public void doStop() {
		this.stopEventDrivenConsumers();
	}

	private void startEventDrivenConsumers() {
		for (final EventDrivenConsumer eventDrivenConsumer : this.eventDrivenConsumers) {
			eventDrivenConsumer.start();
		}
	}

	private void stopEventDrivenConsumers() {
		for (final EventDrivenConsumer eventDrivenConsumer : this.eventDrivenConsumers) {
			eventDrivenConsumer.stop();
		}
	}

}
