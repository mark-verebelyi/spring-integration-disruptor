package org.springframework.integration.disruptor.config.workflow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;

class EventProcessorTrackingRingBuffer<T> {

	private final RingBuffer<T> delegate;
	private final List<EventProcessor> eventProcessors;

	public EventProcessorTrackingRingBuffer(final RingBuffer<T> delegate) {
		this.delegate = delegate;
		this.eventProcessors = new ArrayList<EventProcessor>();
	}

	public RingBuffer<T> getDelegate() {
		return this.delegate;
	}

	public List<EventProcessor> getEventProcessors() {
		return this.eventProcessors;
	}

	public void addEventProcessor(final EventProcessor eventProcessorToAdd) {
		Assert.isTrue(eventProcessorToAdd != null, "Can not add null EventProcessor");
		this.eventProcessors.add(eventProcessorToAdd);
	}

	public void addEventProcessors(final List<EventProcessor> eventProcessorsToAdd) {
		Assert.isTrue(eventProcessorsToAdd != null, "Can not add null EventProcessors");
		for (final EventProcessor eventProcessorToAdd : eventProcessorsToAdd) {
			this.addEventProcessor(eventProcessorToAdd);
		}
	}

}