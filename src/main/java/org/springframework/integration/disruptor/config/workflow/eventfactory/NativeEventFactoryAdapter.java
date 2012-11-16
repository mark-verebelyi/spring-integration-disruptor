package org.springframework.integration.disruptor.config.workflow.eventfactory;

import com.lmax.disruptor.EventFactory;

public final class NativeEventFactoryAdapter<T> implements EventFactory<T> {

	private final EventFactory<T> eventFactory;

	public NativeEventFactoryAdapter(final EventFactory<T> eventFactory) {
		this.eventFactory = eventFactory;
	}

	public T newInstance() {
		return this.eventFactory.newInstance();
	}

}
