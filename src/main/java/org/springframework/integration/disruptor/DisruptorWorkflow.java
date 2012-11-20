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

public final class DisruptorWorkflow<T> implements MessageHandler, SmartLifecycle {

	private volatile boolean running = false;
	private volatile boolean autoStartup = true;

	private final RingBuffer<T> ringBuffer;
	private final Executor executor;
	private final List<EventProcessor> eventProcessors;
	private final MessageEventTranslator<T> messageEventTranslator;
	private final List<EventDrivenConsumer> eventDrivenConsumers;

	public DisruptorWorkflow(final RingBuffer<T> ringBuffer, final Executor executor, final List<EventProcessor> eventProcessors,
			final MessageEventTranslator<T> messageEventTranslator, final List<SubscribableChannel> subscribableChannels) {
		Assert.isTrue(ringBuffer != null, "RingBuffer can not be null");
		Assert.isTrue(executor != null, "Executor can not be null");
		Assert.isTrue(eventProcessors != null, "EventProcessors can not be null");
		Assert.isTrue(messageEventTranslator != null, "MessageEventTranslator can not be null");
		Assert.isTrue(subscribableChannels != null, "SubscribableChannels can not be null");
		this.ringBuffer = ringBuffer;
		this.executor = executor;
		this.eventProcessors = eventProcessors;
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
		new EventPublisher<T>(this.ringBuffer).publishEvent(new EventTranslator<T>() {

			public void translateTo(final T event, final long sequence) {
				DisruptorWorkflow.this.messageEventTranslator.translateTo(message, event);
			}

		});
	}

	public void start() {
		this.startEventProcessors();
		this.startEventDrivenConsumers();
		this.running = true;
	}

	public void stop() {
		this.running = false;
		this.stopEventDrivenConsumers();
		this.stopEventProcessors();
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

	private void startEventProcessors() {
		for (final EventProcessor eventProcessor : this.eventProcessors) {
			this.executor.execute(eventProcessor);
		}
	}

	private void stopEventProcessors() {
		for (final EventProcessor eventProcessor : this.eventProcessors) {
			eventProcessor.halt();
		}
	}

	public boolean isRunning() {
		return this.running;
	}

	public int getPhase() {
		return Integer.MIN_VALUE;
	}

	public boolean isAutoStartup() {
		return this.autoStartup;
	}

	public void stop(final Runnable callback) {
		this.stop();
	}

}
