package org.springframework.integration.disruptor.config;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.ChannelInterceptor;
import org.springframework.integration.disruptor.DisruptorChannel;

public class DisruptorChannelFactoryBean implements FactoryBean<MessageChannel> {

	public void setInterceptors(final List<ChannelInterceptor> interceptors) {
	}

	public MessageChannel getObject() throws Exception {
		return new DisruptorChannel();
	}

	public Class<?> getObjectType() {
		return DisruptorChannel.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
