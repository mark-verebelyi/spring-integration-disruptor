package org.springframework.integration.disruptor;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;

import com.lmax.disruptor.EventPublisher;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;

public class DisruptorWorkflow implements MessageHandler, SmartLifecycle {

	private volatile boolean running = false;
	private volatile boolean autoStartup = true;

	private final RingBuffer<MessagingEvent> ringBuffer;

	public DisruptorWorkflow(final RingBuffer<MessagingEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void handleMessage(final Message<?> message) throws MessagingException {

		new EventPublisher<MessagingEvent>(this.ringBuffer).publishEvent(new EventTranslator<MessagingEvent>() {

			public void translateTo(final MessagingEvent event, final long sequence) {
				event.setPayload(message);
			}

		});

	}

	public void start() {
		this.running = true;
	}

	public void stop() {
		this.running = false;
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
