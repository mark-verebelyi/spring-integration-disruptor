package org.springframework.integration.disruptor.config;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.ChannelInterceptor;
import org.springframework.integration.disruptor.DisruptorChannel;

public class DisruptorChannelFactoryBean implements FactoryBean<MessageChannel> {

	public void setInterceptors(final List<ChannelInterceptor> interceptors) {
	}

	private int size;

	public void setSize(final int size) {
		this.size = size;
	}

	private ClaimStrategy claimStrategy;

	public void setClaimStrategy(final ClaimStrategy claimStrategy) {
		this.claimStrategy = claimStrategy;
	}

	private WaitStrategy waitStrategy;

	public void setWaitStrategy(final WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	public MessageChannel getObject() throws Exception {
		return new DisruptorChannel(this.claimStrategy.newInstance(this.size), this.waitStrategy.newInstance());
	}

	public Class<?> getObjectType() {
		return DisruptorChannel.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
