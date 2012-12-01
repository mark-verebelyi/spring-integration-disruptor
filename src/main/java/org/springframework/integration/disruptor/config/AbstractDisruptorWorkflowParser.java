package org.springframework.integration.disruptor.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.integration.disruptor.config.workflow.HandlerGroupDefinition;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

abstract class AbstractDisruptorWorkflowParser extends AbstractRingBufferParser {

	@Override
	protected final AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(this.getFactoryClass());
		this.parseEventType(element, parserContext, builder);
		this.parseEventFactoryName(element, parserContext, builder);
		final WaitStrategy waitStrategy = this.parseWaitStrategy(element, parserContext);
		builder.addPropertyValue("waitStrategy", waitStrategy);
		final ClaimStrategy claimStrategy = this.parseClaimStrategy(element, parserContext);
		builder.addPropertyValue("claimStrategy", claimStrategy);
		this.parseExecutorName(element, parserContext, builder);
		this.parseHandlerGroups(element, parserContext, builder);
		this.parseTranslator(element, parserContext, builder);
		this.doParseInternal(element, parserContext, builder);
		return builder.getBeanDefinition();
	}

	protected abstract Class<?> getFactoryClass();

	protected abstract void doParseInternal(Element element, ParserContext parserContext, BeanDefinitionBuilder builder);

	private void parseTranslator(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		final String translatorAttribute = element.getAttribute("translator");
		builder.addPropertyValue("translatorName", translatorAttribute);
	}

	private void parseEventFactoryName(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		final String eventFactoryAttribute = element.getAttribute("event-factory");
		builder.addPropertyValue("eventFactoryName", eventFactoryAttribute);
	}

	private void parseEventType(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		final String eventTypeAttribute = element.getAttribute("event-type");
		if (StringUtils.hasText(eventTypeAttribute)) {
			builder.addPropertyValue("eventType", ClassUtils.resolveClassName(eventTypeAttribute, this.getClass().getClassLoader()));
		} else {
			builder.addPropertyValue("eventType", MessagingEvent.class);
		}
	}

	private void parseExecutorName(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		final String executorAttribute = element.getAttribute("executor");
		if (StringUtils.hasText(executorAttribute)) {
			builder.addPropertyValue("executorName", executorAttribute);
			builder.addDependsOn(executorAttribute);
		}
	}

	private void parseHandlerGroups(final Element element, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		final Element handlerGroupsElement = DomUtils.getChildElementByTagName(element, "handler-groups");
		if (handlerGroupsElement != null) {
			final List<Element> handlerGroupElements = DomUtils.getChildElementsByTagName(handlerGroupsElement, "handler-group");
			if (handlerGroupElements.size() > 0) {
				final Map<String, List<BeanDefinitionHolder>> resolvedHandlerMap = new ManagedMap<String, List<BeanDefinitionHolder>>();
				final Map<String, HandlerGroup> handlerGroups = this.parseHandlerGroups(handlerGroupElements, parserContext, builder, resolvedHandlerMap);
				builder.addPropertyValue("handlerGroupDefinition", new HandlerGroupDefinition(handlerGroups));
				builder.addPropertyValue("resolvedHandlerMap", resolvedHandlerMap);
			} else {
				parserContext.getReaderContext().error("At least 1 'handler-group' is mandatory for 'handler-groups'", handlerGroupElements);
			}
		} else {
			parserContext.getReaderContext().error("'handler-groups' element is mandatory for 'workflow'", element);
		}
	}

	private Map<String, HandlerGroup> parseHandlerGroups(final List<Element> handlerGroupElements, final ParserContext parserContext,
			final BeanDefinitionBuilder builder, final Map<String, List<BeanDefinitionHolder>> resolvedHandlers) {
		final List<HandlerGroup> handlerGroups = new ArrayList<HandlerGroup>();
		for (final Element handlerGroupElement : handlerGroupElements) {
			final HandlerGroup handlerGroup = this.parseHandlerGroupElement(handlerGroupElement, parserContext, builder);
			handlerGroups.add(handlerGroup);
			resolvedHandlers.put(handlerGroup.getName(), handlerGroup.getHandlerBeanDefinitions());
		}
		final Map<String, HandlerGroup> handlerGroupMap = new HashMap<String, HandlerGroup>();
		for (final HandlerGroup handlerGroup : handlerGroups) {
			handlerGroupMap.put(handlerGroup.getName(), handlerGroup);
		}
		return handlerGroupMap;
	}

	private HandlerGroup parseHandlerGroupElement(final Element handlerGroupElement, final ParserContext parserContext, final BeanDefinitionBuilder builder) {
		final String group = this.parseHandlerGroupName(handlerGroupElement, parserContext);
		final List<String> dependencies = this.parseHandlerGroupDependencies(handlerGroupElement);
		final List<String> handlerBeanNames = this.parseHandlerBeanNames(handlerGroupElement, parserContext);
		final List<BeanDefinitionHolder> handlerBeanDefinitions = this.parseHandlerBeanDefinitions(handlerGroupElement, parserContext, builder);
		return this.newHandlerGroup(group, dependencies, handlerBeanNames, handlerBeanDefinitions);
	}

	private List<BeanDefinitionHolder> parseHandlerBeanDefinitions(final Element handlerGroupElement, final ParserContext parserContext,
			final BeanDefinitionBuilder builder) {
		final ManagedList<BeanDefinitionHolder> beanDefs = new ManagedList<BeanDefinitionHolder>();
		final List<Element> elements = DomUtils.getChildElementsByTagName(handlerGroupElement, DisruptorNamespaceElements.ELEMENT_FORWARDING_EVENT_HANDLER);
		for (final Element element : elements) {
			final BeanDefinition beanDef = parserContext.getDelegate().parseCustomElement(element, builder.getBeanDefinition());
			final String name = BeanDefinitionReaderUtils.generateBeanName(beanDef, parserContext.getRegistry(), true);
			final BeanDefinitionHolder beanDefHolder = new BeanDefinitionHolder(beanDef, name);
			beanDefs.add(beanDefHolder);
		}
		return beanDefs;
	}

	private HandlerGroup newHandlerGroup(final String group, final List<String> dependencies, final List<String> handlerBeanNames,
			final List<BeanDefinitionHolder> handlerBeanDefinitions) {
		final HandlerGroup handlerGroup = new HandlerGroup();
		handlerGroup.setName(group);
		handlerGroup.setDependencies(dependencies);
		handlerGroup.setHandlerBeanNames(handlerBeanNames);
		handlerGroup.setHandlerBeanDefinitions(handlerBeanDefinitions);
		return handlerGroup;
	}

	private List<String> parseHandlerBeanNames(final Element handlerGroupElement, final ParserContext parserContext) {
		final List<String> handlerBeanNames = new ArrayList<String>();
		final List<Element> handlerElements = DomUtils.getChildElementsByTagName(handlerGroupElement, "handler");
		if (handlerElements.size() > 0) {
			for (final Element handlerElement : handlerElements) {
				final String refAttribute = handlerElement.getAttribute("ref");
				if (StringUtils.hasText(refAttribute)) {
					handlerBeanNames.add(refAttribute);
				} else {
					parserContext.getReaderContext().error("'ref' attribute is mandatory for 'handler'", handlerElement);
				}
			}

		} else {
			parserContext.getReaderContext().error("At least 1 'handler' is mandatory for 'handler-group'", handlerGroupElement);
		}
		return handlerBeanNames;
	}

	private List<String> parseHandlerGroupDependencies(final Element handlerGroupElement) {
		final String waitFor = this.parseHandlerWaitFor(handlerGroupElement);
		final List<String> dependencies = Arrays.asList(waitFor.split(","));
		return dependencies;
	}

	private String parseHandlerWaitFor(final Element handlerGroupElement) {
		final String waitFor = handlerGroupElement.getAttribute("wait-for");
		if (StringUtils.hasText(waitFor)) {
			return waitFor;
		} else {
			return "ring-buffer";
		}
	}

	private String parseHandlerGroupName(final Element handlerGroupElement, final ParserContext parserContext) {
		final String group = handlerGroupElement.getAttribute("group");
		if (!StringUtils.hasText(group)) {
			parserContext.getReaderContext().error("'group' attribute is mandatory for 'handler-group'", handlerGroupElement);
		}
		return group;
	}

}
