package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractChannelParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class DisruptorChannelParser extends AbstractChannelParser {

	@Override
	protected BeanDefinitionBuilder buildBeanDefinition(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DisruptorChannelFactoryBean.class);
		final Element ringBufferElement = DomUtils.getChildElementByTagName(element, "ring-buffer");
		if (ringBufferElement != null) {
			IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, ringBufferElement, "size");
		}
		return builder;
	}

}
