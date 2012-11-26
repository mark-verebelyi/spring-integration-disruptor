package org.springframework.integration.disruptor.config;

import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.MultiThreadedClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

abstract class AbstractRingBufferParser extends AbstractBeanDefinitionParser {

	private final Log log = LogFactory.getLog(AbstractRingBufferParser.class);

	private Integer parseBufferSize(final Element element, final ParserContext parserContext) {
		final String size = element.getAttribute(DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_BUFFER_SIZE);
		if (StringUtils.hasText(size)) {
			this.log.debug("'" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_BUFFER_SIZE + "' attribute was '" + size + "'");
			return Integer.parseInt(size);
		} else {
			parserContext.getReaderContext().error("Attribute '" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_BUFFER_SIZE + "' is mandatory.", element);
			return null;
		}
	}

	public WaitStrategy parseWaitStrategy(final Element element, final ParserContext parserContext) {
		final String waitStrategyAttribute = element.getAttribute(DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_WAIT_STRATEGY);
		if (StringUtils.hasText(waitStrategyAttribute)) {
			this.log.debug("'" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_WAIT_STRATEGY + "' attribute was '" + waitStrategyAttribute + "'");
			final WaitStrategy waitStrategy = parseWaitStrategy(waitStrategyAttribute);
			if (waitStrategy != null) {
				this.log.debug("The following WaitStrategy is used '" + waitStrategy.getClass().getSimpleName() + "'");
				return waitStrategy;
			} else {
				parserContext.getReaderContext().error(
						"Invalid '" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_WAIT_STRATEGY + "' attribute: '" + waitStrategyAttribute + "'", element);
				return null;
			}
		} else {
			final WaitStrategy waitStrategy = new BlockingWaitStrategy();
			this.log.debug("No '" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_WAIT_STRATEGY + "' attribute given, using '"
					+ waitStrategy.getClass().getSimpleName() + "'");
			return waitStrategy;
		}

	}

	public ClaimStrategy parseClaimStrategy(final Element element, final ParserContext parserContext) {
		final String claimStrategyAttribute = element.getAttribute(DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_CLAIM_STRATEGY);
		if (StringUtils.hasText(claimStrategyAttribute)) {
			this.log.debug("'" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_CLAIM_STRATEGY + "' attribute was '" + claimStrategyAttribute + "'");
			final ClaimStrategy claimStrategy = parseClaimStrategy(claimStrategyAttribute, this.parseBufferSize(element, parserContext));
			if (claimStrategy != null) {
				this.log.debug("The following ClaimStrategy is used '" + claimStrategy.getClass().getSimpleName() + "'");
				return claimStrategy;
			} else {
				parserContext.getReaderContext().error(
						"Invalid '" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_CLAIM_STRATEGY + "' attribute: '" + claimStrategyAttribute + "'",
						element);
				return null;
			}
		} else {
			final ClaimStrategy claimStrategy = new MultiThreadedClaimStrategy(this.parseBufferSize(element, parserContext));
			this.log.debug("No '" + DisruptorNamespaceElements.RING_BUFFER_ATTRIBUTE_CLAIM_STRATEGY + "' attribute given, using '"
					+ claimStrategy.getClass().getSimpleName() + "'");
			return claimStrategy;
		}
	}

	private static ClaimStrategy parseClaimStrategy(final String claimStrategy, final int bufferSize) {
		return ClaimStrategies.forName(claimStrategy, bufferSize);
	}

	private static WaitStrategy parseWaitStrategy(final String waitStrategy) {
		return WaitStrategies.forName(waitStrategy);
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