package org.springframework.integration.disruptor.config;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

public class DisruptorNamespaceHandler extends AbstractIntegrationNamespaceHandler {

	public void init() {
		this.registerBeanDefinitionParser("channel", new DisruptorChannelParser());
	}

}
