package org.springframework.integration.disruptor.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.MultiThreadedClaimStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/ring-buffer-1-test-config.xml")
public class RingBufferIntegrationTest {

	private static final int DEFAULT_RING_BUFFER_SIZE = 1024;
	private static final Class<? extends WaitStrategy> DEFAULT_WAIT_STRATEGY_CLASS = BlockingWaitStrategy.class;
	private static final Class<? extends ClaimStrategy> DEFAULT_CLAIM_STRATEGY_CLASS = MultiThreadedClaimStrategy.class;

	@Autowired
	private RingBuffer<?> defaultRingBuffer;

	@Test
	public void Default_RingBuffer() {
		assertEquals(DEFAULT_RING_BUFFER_SIZE, this.defaultRingBuffer.getBufferSize());
		assertTrue(DEFAULT_CLAIM_STRATEGY_CLASS.isAssignableFrom(getClaimStrategyClass(this.defaultRingBuffer)));
		assertTrue(DEFAULT_WAIT_STRATEGY_CLASS.isAssignableFrom(getWaitStrategyClass(this.defaultRingBuffer)));
	}

	private static Class<? extends WaitStrategy> getWaitStrategyClass(final RingBuffer<?> ringBuffer) {
		final WaitStrategy waitStrategy = (WaitStrategy) ReflectionTestUtils.getField(ringBuffer, "waitStrategy");
		return waitStrategy.getClass();
	}

	private static Class<? extends ClaimStrategy> getClaimStrategyClass(final RingBuffer<?> ringBuffer) {
		final ClaimStrategy claimStrategy = (ClaimStrategy) ReflectionTestUtils.getField(ringBuffer, "claimStrategy");
		return claimStrategy.getClass();
	}
}
