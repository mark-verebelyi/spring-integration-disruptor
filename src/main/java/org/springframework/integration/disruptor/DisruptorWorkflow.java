package org.springframework.integration.disruptor;

import org.springframework.context.SmartLifecycle;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;

public class DisruptorWorkflow<Event> implements MessageHandler, SmartLifecycle {

	private volatile boolean running = false;
	private volatile boolean autoStartup = true;

	public void handleMessage(final Message<?> message) throws MessagingException {
		System.out.println("Message: " + message);
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
