package org.springframework.integration.disruptor;

import com.lmax.disruptor.EventHandler;

public class TrackedNamedEventHandler implements EventHandler<MessagingEvent> {

	private final String handlerName;
	private final HandlerOrderTracker handlerOrderTracker;

	public TrackedNamedEventHandler(final String handlerName, final HandlerOrderTracker handlerOrderTracker) {
		this.handlerName = handlerName;
		this.handlerOrderTracker = handlerOrderTracker;
	}

	public void onEvent(final MessagingEvent event, final long sequence, final boolean endOfBatch) throws Exception {
		this.handlerOrderTracker.addHandlerName(this.handlerName);
	}

}
