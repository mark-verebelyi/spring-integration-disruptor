package org.springframework.integration.disruptor.config;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinitionHolder;

import com.lmax.disruptor.EventProcessor;

public final class HandlerGroup {

	private String name;
	private List<String> dependencies;
	private List<String> handlerBeanNames;
	private List<BeanDefinitionHolder> handlerBeanDefinitions;
	private List<EventProcessor> eventProcessors;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<String> getDependencies() {
		return this.dependencies;
	}

	public void setDependencies(final List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public List<String> getHandlerBeanNames() {
		return this.handlerBeanNames;
	}

	public void setHandlerBeanNames(final List<String> handlerBeanNames) {
		this.handlerBeanNames = handlerBeanNames;
	}

	public List<BeanDefinitionHolder> getHandlerBeanDefinitions() {
		return this.handlerBeanDefinitions;
	}

	public void setHandlerBeanDefinitions(final List<BeanDefinitionHolder> handlerBeanDefinitions) {
		this.handlerBeanDefinitions = handlerBeanDefinitions;
	}

	public List<EventProcessor> getEventProcessors() {
		return this.eventProcessors;
	}

	public void setEventProcessors(final List<EventProcessor> eventProcessors) {
		this.eventProcessors = eventProcessors;
	}

	public boolean hasSingleDependency(final String name) {
		return (this.dependencies.size() == 1) && name.equals(this.dependencies.get(0));
	}

	@Override
	public String toString() {
		return "HandlerGroup [name=" + this.name + ", dependencies=" + this.dependencies + ", handlerBeanNames=" + this.handlerBeanNames
				+ ", handlerBeanDefinitions=" + this.handlerBeanDefinitions + ", eventProcessors=" + this.eventProcessors + "]";
	}

}