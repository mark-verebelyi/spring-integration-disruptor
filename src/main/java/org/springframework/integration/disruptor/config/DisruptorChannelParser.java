package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractChannelParser;
import org.springframework.integration.disruptor.DisruptorChannel;
import org.springframework.util.xml.DomUtils;
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

public class DisruptorChannelParser extends AbstractChannelParser {

	@Override
	protected BeanDefinitionBuilder buildBeanDefinition(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DisruptorChannel.class);
		final Element ringBufferElement = DomUtils.getChildElementByTagName(element, "ring-buffer");
		if (ringBufferElement != null) {
			final ClaimStrategy claimStrategy = parseClaimStrategy(ringBufferElement);
			builder.addConstructorArgValue(claimStrategy);
			final WaitStrategy waitStrategy = parseWaitStrategy(ringBufferElement);
			builder.addConstructorArgValue(waitStrategy);
		}
		return builder;
	}

	private static WaitStrategy parseWaitStrategy(final Element ringBufferElement) {
		final String waitStrategy = ringBufferElement.getAttribute("wait-strategy");
		return parseWaitStrategy(waitStrategy);
	}

	private static ClaimStrategy parseClaimStrategy(final Element ringBufferElement) {
		final String bufferSize = ringBufferElement.getAttribute("buffer-size");
		final String claimStrategy = ringBufferElement.getAttribute("claim-strategy");
		return parseClaimStrategy(claimStrategy, Integer.parseInt(bufferSize));
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
