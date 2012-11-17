package org.springframework.integration.disruptor;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.EventPublisher;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;

public class DisruptorWorkflow<T> implements MessageHandler, SmartLifecycle {

	private volatile boolean running = false;
	private volatile boolean autoStartup = true;

	private final RingBuffer<T> ringBuffer;
	private final Executor executor;
	private final List<EventProcessor> eventProcessors;

	public DisruptorWorkflow(final RingBuffer<T> ringBuffer, final Executor executor, final List<EventProcessor> eventProcessors) {
		this.ringBuffer = ringBuffer;
		this.executor = executor;
		this.eventProcessors = eventProcessors;
	}

	public void handleMessage(final Message<?> message) throws MessagingException {

		new EventPublisher<T>(this.ringBuffer).publishEvent(new EventTranslator<T>() {

			public void translateTo(final T event, final long sequence) {
				final MessagingEvent me = (MessagingEvent) event;
				me.setPayload(message);
			}

		});

	}

	public void start() {
		this.startEventProcessors();
		this.running = true;
	}

	public void stop() {
		this.running = false;
		this.stopEventProcessors();
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
