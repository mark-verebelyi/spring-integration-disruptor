package org.springframework.integration.disruptor.config;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public enum WaitStrategies {

	BLOCKING("blocking") {

		@Override
		public WaitStrategy newInstance() {
			return new BlockingWaitStrategy();
		}

	},

	BUSY_SPIN("busy-spin") {

		@Override
		public WaitStrategy newInstance() {
			return new BusySpinWaitStrategy();
		}

	},

	YIELDING("yielding") {

		@Override
		public WaitStrategy newInstance() {
			return new YieldingWaitStrategy();
		}

	},

	SLEEPING("sleeping") {

		@Override
		public WaitStrategy newInstance() {
			return new SleepingWaitStrategy();
		}

	};

	private String name;

	private WaitStrategies(final String name) {
		this.name = name;
	}

	public static WaitStrategies find(final String name) {
		if (BLOCKING.name.equals(name)) {
			return BLOCKING;
		} else if (BUSY_SPIN.name.equals(name)) {
			return BUSY_SPIN;
		} else if (YIELDING.name.equals(name)) {
			return YIELDING;
		} else if (SLEEPING.name.equals(name)) {
			return SLEEPING;
		} else {
			return null;
		}
	}

	public static WaitStrategy forName(final String name) {
		final WaitStrategies found = find(name);
		if (found != null) {
			return found.newInstance();
		} else {
			return null;
		}
	}

	public abstract WaitStrategy newInstance();

}
