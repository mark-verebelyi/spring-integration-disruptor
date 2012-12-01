package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.integration.disruptor.ForwardingEventHandler;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public final class ForwardingEventHandlerParser extends AbstractBeanDefinitionParser {

	private static final String FOWARDING_EVENT_HANDLER_ATTRIBUTE_CHANNEL = "channel";
	private static final String FOWARDING_EVENT_HANDLER_ATTRIBUTE_CONVERTER = "converter";
	private static final String FOWARDING_EVENT_HANDLER_ATTRIBUTE_TRANSFORMER = "transformer";

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ForwardingEventHandler.class);
		this.parseChannel(element, parserContext, builder);
		this.parseConverter(element, parserContext, builder);
		this.parseTransformer(element, parserContext, builder);
		return builder.getBeanDefinition();
	}

	private void parseChannel(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		parseMandatoryBeanReference(element, parserContext, builder, FOWARDING_EVENT_HANDLER_ATTRIBUTE_CHANNEL);
	}

	private void parseConverter(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		parseOptionalBeanReference(element, parserContext, builder, FOWARDING_EVENT_HANDLER_ATTRIBUTE_CONVERTER);
	}

	private void parseTransformer(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		parseOptionalBeanReference(element, parserContext, builder, FOWARDING_EVENT_HANDLER_ATTRIBUTE_TRANSFORMER);
	}

	private static void parseMandatoryBeanReference(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder,
			final String attributeName) {
		parseBeanReference(element, parserContext, builder, attributeName, true);
	}

	private static void parseOptionalBeanReference(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder,
			final String attributeName) {
		parseBeanReference(element, parserContext, builder, attributeName, false);
	}

	private static void parseBeanReference(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder,
			final String attributeName, final boolean mandatory) {
		final String beanName = element.getAttribute(attributeName);
		if (StringUtils.hasText(beanName)) {
			builder.addPropertyReference(Conventions.attributeNameToPropertyName(attributeName), beanName);
		} else {
			if (mandatory) {
				parserContext.getReaderContext().error("'" + attributeName + "' attribute is mandatory.", element);
			}
		}
	}

}
