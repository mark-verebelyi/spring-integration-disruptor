package org.springframework.integration.disruptor.config;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.MultiThreadedClaimStrategy;
import com.lmax.disruptor.MultiThreadedLowContentionClaimStrategy;
import com.lmax.disruptor.SingleThreadedClaimStrategy;

public enum ClaimStrategies {

	MULTI_THREADED("multi-threaded") {

		@Override
		public ClaimStrategy newInstance(final int bufferSize) {
			return new MultiThreadedClaimStrategy(bufferSize);
		}

	},

	MULTI_THREADED_LOW_CONTENTION("multi-threaded-low-contention") {

		@Override
		public ClaimStrategy newInstance(final int bufferSize) {
			return new MultiThreadedLowContentionClaimStrategy(bufferSize);
		}

	},

	SINGLE_THREADED("single-threaded") {

		@Override
		public ClaimStrategy newInstance(final int bufferSize) {
			return new SingleThreadedClaimStrategy(bufferSize);
		}

	};

	private String name;

	private ClaimStrategies(final String name) {
		this.name = name;
	}

	public static ClaimStrategies find(final String name) {
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

	public static ClaimStrategy forName(final String name, final int bufferSize) {
		final ClaimStrategies found = find(name);
		if (found != null) {
			return found.newInstance(bufferSize);
		} else {
			return null;
		}
	}

	public abstract ClaimStrategy newInstance(int bufferSize);

}
