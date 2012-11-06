package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractChannelParser;
import org.w3c.dom.Element;

public class DisruptorChannelParser extends AbstractChannelParser {

	@Override
	protected BeanDefinitionBuilder buildBeanDefinition(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DisruptorChannelFactoryBean.class);
		return beanDefinitionBuilder;
	}

}
