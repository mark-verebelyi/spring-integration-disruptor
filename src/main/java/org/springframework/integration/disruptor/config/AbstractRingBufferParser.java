package org.springframework.integration.disruptor.config;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

abstract class AbstractRingBufferParser extends AbstractBeanDefinitionParser {

	private static Integer parseBufferSize(final Element element) {
		final String size = element.getAttribute(DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_SIZE);
		if (StringUtils.hasText(size)) {
			return Integer.parseInt(size);
		} else {
			return null;
		}
	}

	private static ClaimStrategy parseClaimStrategy(final String claimStrategy, final int bufferSize) {
		return ClaimStrategies.forName(claimStrategy, bufferSize);
	}

	private static WaitStrategy parseWaitStrategy(final String waitStrategy) {
		return WaitStrategies.forName(waitStrategy);
	}

	protected void parseWaitStrategy(final Element element, final BeanDefinitionBuilder builder) {
		final String waitStrategy = element.getAttribute(DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_WAIT_STRATEGY);
		if (StringUtils.hasText(waitStrategy)) {
			builder.addConstructorArgValue(parseWaitStrategy(waitStrategy));
		}
	}

	protected void parseClaimStrategy(final Element element, final BeanDefinitionBuilder builder) {
		final int bufferSize = parseBufferSize(element);
		final String claimStrategy = element.getAttribute(DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_CLAIM_STRATEGY);
		if (StringUtils.hasText(claimStrategy)) {
			builder.addConstructorArgValue(parseClaimStrategy(claimStrategy, bufferSize));
		}
	}

	protected void parseEventFactory(final Element element, final BeanDefinitionBuilder builder) {
		final String eventFactory = element.getAttribute(DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_EVENT_FACTORY);
		if (StringUtils.hasText(eventFactory)) {
			builder.addConstructorArgReference(eventFactory);
		} else {
			builder.addConstructorArgValue(MessagingEvent.newEventFactory());
		}
	}

	protected void parseExecutor(final Element element, final BeanDefinitionBuilder builder) {
		final String executor = element.getAttribute(DisruptorNamespaceElements.DISRUPTOR_ATTRIBUTE_EXECUTOR);
		if (StringUtils.hasText(executor)) {
			builder.addConstructorArgReference(executor);
		} else {
			builder.addConstructorArgValue(Executors.newSingleThreadExecutor());
		}
	}
}