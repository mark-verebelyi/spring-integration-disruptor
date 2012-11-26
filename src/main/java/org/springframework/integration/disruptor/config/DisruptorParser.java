package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorParser extends AbstractRingBufferParser {

	@Override
	protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Disruptor.class);
		this.parseEventFactory(element, builder);
		this.parseExecutor(element, builder);
		final ClaimStrategy claimStrategy = this.parseClaimStrategy(element, parserContext);
		builder.addConstructorArgValue(claimStrategy);
		final WaitStrategy waitStrategy = this.parseWaitStrategy(element, parserContext);
		builder.addConstructorArgValue(waitStrategy);
		return builder.getBeanDefinition();
	}

}
