package org.springframework.integration.disruptor.config.workflow.eventfactory;

import org.springframework.beans.BeanUtils;

import com.lmax.disruptor.EventFactory;

public class FallbackEventFactoryAdapter<T> implements EventFactory<T> {

	private final Class<T> expectedType;

	public FallbackEventFactoryAdapter(final Class<T> expectedType) {
		this.expectedType = expectedType;
	}

	public T newInstance() {
		return BeanUtils.instantiate(this.expectedType);
	}
}
