package org.springframework.integration.disruptor.config;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.ChannelInterceptor;
import org.springframework.integration.disruptor.DisruptorChannel;

import com.lmax.disruptor.WaitStrategy;

public class DisruptorChannelFactoryBean implements FactoryBean<MessageChannel> {

	private int ringBufferSize;

	public void setSize(final int ringBufferSize) {
		this.ringBufferSize = ringBufferSize;
	}

	private WaitStrategy waitStrategy;

	public void setWaitStrategy(final WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	public void setInterceptors(final List<ChannelInterceptor> interceptors) {
	}

	public MessageChannel getObject() throws Exception {
		return new DisruptorChannel(this.ringBufferSize, this.waitStrategy);
	}

	public Class<?> getObjectType() {
		return DisruptorChannel.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
