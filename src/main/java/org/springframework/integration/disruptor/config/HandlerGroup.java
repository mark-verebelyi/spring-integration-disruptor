package org.springframework.integration.disruptor.config;

import java.util.List;

public final class HandlerGroup {

	private String name;
	private List<String> dependencies;
	private List<String> handlerBeanNames;

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

	public boolean hasSingleDependency(final String name) {
		return (this.dependencies.size() == 1) && name.equals(this.dependencies.get(0));
	}

	@Override
	public String toString() {
		return "HandlerGroup [name=" + this.name + ", dependencies=" + this.dependencies + ", handlerBeanNames=" + this.handlerBeanNames + "]";
	}

}