package org.springframework.integration.disruptor.config.workflow;

import java.util.List;

import org.springframework.integration.disruptor.config.workflow.DependencyGraphImpl.DependencySetter;

interface DependencyGraph<T> extends Graph {

	List<String> getDependencies(String depender);

	List<String> getSymbolicNames();

	List<String> toSymbolicNames(Iterable<Integer> keys);

	DependencySetter addDependency(String depender);

	DependencyGraph<T> inverse();

	List<String> getOrphanDependencies();

	T getData(String name);

	void putData(String name, T data);

}