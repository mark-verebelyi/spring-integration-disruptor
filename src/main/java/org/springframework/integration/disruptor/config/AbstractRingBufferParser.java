package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

abstract class AbstractRingBufferParser extends AbstractBeanDefinitionParser {

	private static final int DEFAULT_RING_BUFFER_SIZE = 1024;

	private static int parseBufferSize(final Element element) {
		final String size = element.getAttribute("size");
		if (StringUtils.hasText(size)) {
			return Integer.parseInt(size);
		} else {
			return DEFAULT_RING_BUFFER_SIZE;
		}
	}

	private static ClaimStrategy parseClaimStrategy(final String claimStrategy, final int bufferSize) {
		return ClaimStrategies.forName(claimStrategy, bufferSize);
	}

	private static WaitStrategy parseWaitStrategy(final String waitStrategy) {
		return WaitStrategies.forName(waitStrategy);
	}

	protected void parseWaitStrategy(final Element element, final BeanDefinitionBuilder builder) {
		final String waitStrategy = element.getAttribute("wait-strategy");
		if (StringUtils.hasText(waitStrategy)) {
			builder.addConstructorArgValue(parseWaitStrategy(waitStrategy));
		}
	}

	protected void parseClaimStrategy(final Element element, final BeanDefinitionBuilder builder) {
		final int bufferSize = parseBufferSize(element);
		final String claimStrategy = element.getAttribute("claim-strategy");
		if (StringUtils.hasText(claimStrategy)) {
			builder.addConstructorArgValue(parseClaimStrategy(claimStrategy, bufferSize));
		}
	}

	protected void parseEventFactory(final Element element, final BeanDefinitionBuilder builder) {
		final String eventFactory = element.getAttribute("event-factory");
		if (StringUtils.hasText(eventFactory)) {
			builder.addConstructorArgReference(eventFactory);
		} else {
			builder.addConstructorArgValue(MessagingEvent.newEventFactory());
		}
	}

}