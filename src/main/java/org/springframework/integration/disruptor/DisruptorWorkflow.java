package org.springframework.integration.disruptor;

import com.lmax.disruptor.RingBuffer;

public class DisruptorWorkflow<Event> {

	private RingBuffer<Event> ringBuffer;

}
