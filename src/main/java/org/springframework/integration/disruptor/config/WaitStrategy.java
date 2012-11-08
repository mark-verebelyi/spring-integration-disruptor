package org.springframework.integration.disruptor.config;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public enum WaitStrategy {

	BLOCKING("blocking") {

		@Override
		public com.lmax.disruptor.WaitStrategy newInstance() {
			return new BlockingWaitStrategy();
		}

	},

	BUSY_SPIN("busy-spin") {

		@Override
		public com.lmax.disruptor.WaitStrategy newInstance() {
			return new BusySpinWaitStrategy();
		}

	},

	SLEEPING("sleeping") {

		@Override
		public com.lmax.disruptor.WaitStrategy newInstance() {
			return new SleepingWaitStrategy();
		}

	},

	YIELDING("yielding") {

		@Override
		public com.lmax.disruptor.WaitStrategy newInstance() {
			return new YieldingWaitStrategy();
		}

	};

	private final String name;

	private WaitStrategy(final String name) {
		this.name = name;
	}

	public static WaitStrategy fromName(final String name) {
		if (BLOCKING.name.equals(name)) {
			return BLOCKING;
		} else if (BUSY_SPIN.name.equals(name)) {
			return BUSY_SPIN;
		} else if (SLEEPING.name.equals(name)) {
			return SLEEPING;
		} else if (YIELDING.name.equals(name)) {
			return YIELDING;
		} else {
			return null;
		}
	}

	public abstract com.lmax.disruptor.WaitStrategy newInstance();

}
