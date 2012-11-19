package org.springframework.integration.disruptor.config.workflow.eventhandler;

import com.lmax.disruptor.EventHandler;

public final class MethodInvokingEventHandler<T> implements EventHandler<T> {

	public void onEvent(final T event, final long sequence, final boolean endOfBatch) throws Exception {
	}

}
