package org.springframework.integration.disruptor.config;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

public class DisruptorNamespaceHandler extends AbstractIntegrationNamespaceHandler {

	public void init() {
		this.registerBeanDefinitionParser("disruptor", new DisruptorParser());
		this.registerBeanDefinitionParser("ring-buffer", new RingBufferParser());
		this.registerBeanDefinitionParser("channel", new DisruptorChannelParser());
		this.registerBeanDefinitionParser("messaging-event-factory", new MessagingEventFactoryParser());
		this.registerBeanDefinitionParser("message-driven-workflow", new MessageDrivenDisruptorWorkflowParser());
		this.registerBeanDefinitionParser("workflow", new DisruptorWorkflowParser());
	}

}
