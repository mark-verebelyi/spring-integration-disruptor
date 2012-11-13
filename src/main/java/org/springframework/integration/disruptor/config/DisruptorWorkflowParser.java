package org.springframework.integration.disruptor.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public final class DisruptorWorkflowParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DisruptorWorkflowFactoryBean.class);
		this.parsePublisherChannelNames(element, parserContext, builder);
		return builder.getBeanDefinition();
	}

	private void parsePublisherChannelNames(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		final Element parent = DomUtils.getChildElementByTagName(element, "publisher-channels");
		if (parent != null) {
			final Set<String> publisherChannelNames = this.parsePublisherChannelNames(parent, parserContext);
			builder.addPropertyValue("publisherChannelNames", publisherChannelNames);
		}
	}

	private Set<String> parsePublisherChannelNames(final Element parent, final ParserContext parserContext) {
		final Set<String> publisherChannelNames = new HashSet<String>();
		final List<Element> children = DomUtils.getChildElementsByTagName(parent, "publisher-channel");
		for (final Element child : children) {
			final String publisherChannelRef = child.getAttribute("ref");
			if (StringUtils.hasText(publisherChannelRef)) {
				publisherChannelNames.add(publisherChannelRef);
			} else {
				parserContext.getReaderContext().error("'ref' attribute of publisher-channel is mandatory", child);
			}
		}
		return publisherChannelNames;
	}

}
