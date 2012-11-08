package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractChannelParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class DisruptorChannelParser extends AbstractChannelParser {

	@Override
	protected BeanDefinitionBuilder buildBeanDefinition(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DisruptorChannelFactoryBean.class);
		final Element ringBufferElement = DomUtils.getChildElementByTagName(element, "ring-buffer");
		if (ringBufferElement != null) {
			parseBasicAttributes(builder, ringBufferElement, parserContext);
			parseWaitStrategy(builder, ringBufferElement, parserContext);
			parseClaimStrategy(builder, ringBufferElement, parserContext);
		}
		return builder;
	}

	private static void parseBasicAttributes(final BeanDefinitionBuilder builder, final Element ringBufferElement, final ParserContext parserContext) {
		IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, ringBufferElement, "size");
	}

	private static void parseClaimStrategy(final BeanDefinitionBuilder builder, final Element ringBufferElement, final ParserContext parserContext) {
		final String claimStrategy = ringBufferElement.getAttribute("claim-strategy");
		if (StringUtils.hasText(claimStrategy)) {
			builder.addPropertyValue("claimStrategy", ClaimStrategy.fromName(claimStrategy));
		}
	}

	private static void parseWaitStrategy(final BeanDefinitionBuilder builder, final Element ringBufferElement, final ParserContext parserContext) {
		final String waitStrategy = ringBufferElement.getAttribute("wait-strategy");
		if (StringUtils.hasText(waitStrategy)) {
			builder.addPropertyValue("waitStrategy", WaitStrategy.fromName(waitStrategy));
		}
	}
}
