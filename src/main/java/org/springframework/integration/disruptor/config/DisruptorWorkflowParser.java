package org.springframework.integration.disruptor.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.disruptor.config.workflow.DisruptorWorkflowFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public final class DisruptorWorkflowParser extends AbstractDisruptorWorkflowParser {

	@Override
	protected void doParseInternal(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		this.parseInterface(element, builder);
	}

	private void parseInterface(final Element element, final BeanDefinitionBuilder builder) {
		final String interfaceAttribute = element.getAttribute("interface");
		if (StringUtils.hasText(interfaceAttribute)) {
			final Class<?> interfaceClass = ClassUtils.resolveClassName(interfaceAttribute, this.getClass().getClassLoader());
			builder.addPropertyValue("interfaceClass", interfaceClass);
		}
	}

	@Override
	protected Class<?> getFactoryClass() {
		return DisruptorWorkflowFactoryBean.class;
	}

}
