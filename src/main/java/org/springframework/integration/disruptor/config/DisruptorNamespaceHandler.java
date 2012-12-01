package org.springframework.integration.disruptor.config;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

public class DisruptorNamespaceHandler extends AbstractIntegrationNamespaceHandler {

	public void init() {
		this.registerBeanDefinitionParser(DisruptorNamespaceElements.ELEMENT_DISRUPTOR, new DisruptorParser());
		this.registerBeanDefinitionParser(DisruptorNamespaceElements.ELEMENT_RING_BUFFER, new RingBufferParser());
		this.registerBeanDefinitionParser(DisruptorNamespaceElements.ELEMENT_CHANNEL, new DisruptorChannelParser());
		this.registerBeanDefinitionParser(DisruptorNamespaceElements.ELEMENT_MESSAGING_EVENT_FACTORY, new MessagingEventFactoryParser());
		this.registerBeanDefinitionParser(DisruptorNamespaceElements.ELEMENT_MESSAGE_DRIVEN_WORKFLOW, new MessageDrivenDisruptorWorkflowParser());
		this.registerBeanDefinitionParser(DisruptorNamespaceElements.ELEMENT_WORKFLOW, new DisruptorWorkflowParser());
		this.registerBeanDefinitionParser(DisruptorNamespaceElements.ELEMENT_FORWARDING_EVENT_HANDLER, new ForwardingEventHandlerParser());
	}

}
