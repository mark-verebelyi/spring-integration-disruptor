package org.springframework.integration.disruptor.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorFactoryBean<T> extends AbstractFactoryBean<Disruptor<T>> implements InitializingBean, DisposableBean {

	private EventFactory<T> eventFactory;

	public void setEventFactory(final EventFactory<T> eventFactory) {
		this.eventFactory = eventFactory;
	}

	private ClaimStrategy claimStrategy;

	public void setClaimStrategy(final ClaimStrategy claimStrategy) {
		this.claimStrategy = claimStrategy;
	}

	private WaitStrategy waitStrategy;

	public void setWaitStrategy(final WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	private Executor executor;

	public void setExecutor(final Executor executor) {
		this.executor = executor;
	}

	@Override
	protected Disruptor<T> createInstance() throws Exception {
		final Disruptor<T> disruptor = new Disruptor<T>(this.eventFactory, this.executor, this.claimStrategy, this.waitStrategy);
		disruptor.start();
		return disruptor;
	}

	@Override
	protected void destroyInstance(final Disruptor<T> instance) throws Exception {
		instance.shutdown();
	}

	@Override
	public Class<?> getObjectType() {
		return Disruptor.class;
	}

}
