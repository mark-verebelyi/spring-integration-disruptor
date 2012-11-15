package org.springframework.integration.disruptor;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.EventPublisher;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;

public class DisruptorWorkflow implements MessageHandler, SmartLifecycle {

	private volatile boolean running = false;
	private volatile boolean autoStartup = true;

	private final RingBuffer<MessagingEvent> ringBuffer;
	private final ExecutorService executor;
	private final List<EventProcessor> eventProcessors;

	public DisruptorWorkflow(final RingBuffer<MessagingEvent> ringBuffer, final ExecutorService executor, final List<EventProcessor> eventProcessors) {
		this.ringBuffer = ringBuffer;
		this.executor = executor;
		this.eventProcessors = eventProcessors;
	}

	public void handleMessage(final Message<?> message) throws MessagingException {

		new EventPublisher<MessagingEvent>(this.ringBuffer).publishEvent(new EventTranslator<MessagingEvent>() {

			public void translateTo(final MessagingEvent event, final long sequence) {
				event.setPayload(message);
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
		this.executor.shutdown();
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
