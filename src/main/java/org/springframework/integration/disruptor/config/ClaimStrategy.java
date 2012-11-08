package org.springframework.integration.disruptor.config;

import com.lmax.disruptor.MultiThreadedClaimStrategy;
import com.lmax.disruptor.MultiThreadedLowContentionClaimStrategy;
import com.lmax.disruptor.SingleThreadedClaimStrategy;

public enum ClaimStrategy {

	MULTI_THREADED("multi-threaded") {

		@Override
		public com.lmax.disruptor.ClaimStrategy newInstance(final int size) {
			return new MultiThreadedClaimStrategy(size);
		}

	},

	MULTI_THREADED_LOW_CONTENTION("multi-threaded-low-contention") {

		@Override
		public com.lmax.disruptor.ClaimStrategy newInstance(final int size) {
			return new MultiThreadedLowContentionClaimStrategy(size);
		}

	},

	SINGLE_THREADED("single-threaded") {

		@Override
		public com.lmax.disruptor.ClaimStrategy newInstance(final int size) {
			return new SingleThreadedClaimStrategy(size);
		}

	};

	private String name;

	private ClaimStrategy(final String name) {
		this.name = name;
	}

	public static ClaimStrategy fromName(final String name) {
		if (MULTI_THREADED.name.equals(name)) {
			return MULTI_THREADED;
		} else if (MULTI_THREADED_LOW_CONTENTION.name.equals(name)) {
			return MULTI_THREADED_LOW_CONTENTION;
		} else if (SINGLE_THREADED.name.equals(name)) {
			return SINGLE_THREADED;
		} else {
			return null;
		}
	}

	public abstract com.lmax.disruptor.ClaimStrategy newInstance(int size);

}
