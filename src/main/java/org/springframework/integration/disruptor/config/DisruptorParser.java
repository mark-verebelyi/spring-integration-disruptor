package org.springframework.integration.disruptor.config;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorParser extends AbstractRingBufferParser {

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Disruptor.class);
		this.parseEventFactory(element, builder);
		this.parseExecutor(element, builder);
		this.parseClaimStrategy(element, builder);
		this.parseWaitStrategy(element, builder);
		return builder.getBeanDefinition();
	}

	private void parseExecutor(final Element element, final BeanDefinitionBuilder builder) {
		final String executor = element.getAttribute("executor");
		if (StringUtils.hasText(executor)) {
			builder.addConstructorArgReference(executor);
		} else {
			builder.addConstructorArgValue(Executors.newSingleThreadExecutor());
		}
	}

}
