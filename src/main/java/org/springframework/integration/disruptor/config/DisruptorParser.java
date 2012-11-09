package org.springframework.integration.disruptor.config;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.disruptor.GenericEvent;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.MultiThreadedClaimStrategy;
import com.lmax.disruptor.MultiThreadedLowContentionClaimStrategy;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public class DisruptorParser extends AbstractBeanDefinitionParser {

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DisruptorFactoryBean.class);

		final String executor = element.getAttribute("executor");
		if (StringUtils.hasText(executor)) {
			builder.addPropertyReference("executor", executor);
		} else {
			builder.addPropertyValue("executor", Executors.newSingleThreadExecutor());
		}

		final String eventFactory = element.getAttribute("event-factory");
		if (StringUtils.hasText(eventFactory)) {
			builder.addPropertyReference("eventFactory", eventFactory);
		} else {
			builder.addPropertyValue("eventFactory", GenericEvent.newEventFactory());
		}

		final int bufferSize = parseBufferSize(element);

		final String waitStrategy = element.getAttribute("wait-strategy");
		if (StringUtils.hasText(waitStrategy)) {
			builder.addPropertyValue("waitStrategy", parseWaitStrategy(waitStrategy));
		}

		final String claimStrategy = element.getAttribute("claim-strategy");
		if (StringUtils.hasText(claimStrategy)) {
			builder.addPropertyValue("claimStrategy", parseClaimStrategy(claimStrategy, bufferSize));
		}

		return builder.getBeanDefinition();

	}

	private static int parseBufferSize(final Element element) {
		final String size = element.getAttribute("size");
		if (StringUtils.hasText(size)) {
			return Integer.parseInt(size);
		} else {
			return 1024;
		}
	}

	private static ClaimStrategy parseClaimStrategy(final String claimStrategy, final int bufferSize) {
		if ("multi-threaded".equals(claimStrategy)) {
			return new MultiThreadedClaimStrategy(bufferSize);
		} else if ("multi-threaded-low-contention".equals(claimStrategy)) {
			return new MultiThreadedLowContentionClaimStrategy(bufferSize);
		} else if ("single-threaded".equals(claimStrategy)) {
			return new SingleThreadedClaimStrategy(bufferSize);
		} else {
			return null;
		}
	}

	private static WaitStrategy parseWaitStrategy(final String waitStrategy) {
		if ("blocking".equals(waitStrategy)) {
			return new BlockingWaitStrategy();
		} else if ("busy-spin".equals(waitStrategy)) {
			return new BusySpinWaitStrategy();
		} else if ("sleeping".equals(waitStrategy)) {
			return new SleepingWaitStrategy();
		} else if ("yielding".equals(waitStrategy)) {
			return new YieldingWaitStrategy();
		} else {
			return null;
		}
	}

}
