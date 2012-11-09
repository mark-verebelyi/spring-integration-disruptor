package org.springframework.integration.disruptor;

import com.lmax.disruptor.EventFactory;

public final class GenericEvent {

	private Object payload;

	public Object getPayload() {
		return this.payload;
	}

	public void setPayload(final Object payload) {
		this.payload = payload;
	}

	public static EventFactory<GenericEvent> newEventFactory() {
		return new EventFactory<GenericEvent>() {

			public GenericEvent newInstance() {
				return new GenericEvent();
			}

		};
	}

}