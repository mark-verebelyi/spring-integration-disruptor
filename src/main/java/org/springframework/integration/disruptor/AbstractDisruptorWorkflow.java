package org.springframework.integration.disruptor;

import org.springframework.context.SmartLifecycle;

abstract class AbstractDisruptorWorkflow<T> implements SmartLifecycle {

	private volatile boolean running = false;
	private volatile boolean autoStartup = true;
	private volatile int phase = 0;

	public final void start() {
		this.doStart();
		this.running = true;
	}

	protected abstract void doStart();

	public final void stop() {
		this.running = false;
		this.doStop();
	}

	protected abstract void doStop();

	public final boolean isRunning() {
		return this.running;
	}

	public final int getPhase() {
		return this.phase;
	}

	public final boolean isAutoStartup() {
		return this.autoStartup;
	}

	public final void stop(final Runnable callback) {
		this.stop();
		callback.run();
	}

	public void setPhase(final int phase) {
		this.phase = phase;
	}

	public void setAutoStartup(final boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

}
