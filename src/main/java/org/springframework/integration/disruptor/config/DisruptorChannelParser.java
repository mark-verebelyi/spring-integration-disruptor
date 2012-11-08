package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractChannelParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public class DisruptorChannelParser extends AbstractChannelParser {

	@Override
	protected BeanDefinitionBuilder buildBeanDefinition(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DisruptorChannelFactoryBean.class);
		final Element ringBufferElement = DomUtils.getChildElementByTagName(element, "ring-buffer");
		if (ringBufferElement != null) {
			IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, ringBufferElement, "size");
			parseWaitStrategy(builder, ringBufferElement, parserContext);
		}
		return builder;
	}

	private static void parseWaitStrategy(final BeanDefinitionBuilder builder, final Element ringBufferElement, final ParserContext parserContext) {
		final String waitStrategy = ringBufferElement.getAttribute("wait-strategy");
		if (StringUtils.hasText(waitStrategy)) {
			if ("blocking".equals(waitStrategy)) {
				builder.addPropertyValue("waitStrategy", new BlockingWaitStrategy());
			} else if ("busy-spin".equals(waitStrategy)) {
				builder.addPropertyValue("waitStrategy", new BusySpinWaitStrategy());
			} else if ("sleeping".equals(waitStrategy)) {
				builder.addPropertyValue("waitStrategy", new SleepingWaitStrategy());
			} else if ("yielding".equals(waitStrategy)) {
				builder.addPropertyValue("waitStrategy", new YieldingWaitStrategy());
			} else {
				parserContext.getReaderContext().error("Invalid wait strategy: " + waitStrategy, ringBufferElement);
			}
		}
	}
}
