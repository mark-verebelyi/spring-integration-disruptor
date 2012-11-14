package org.springframework.integration.disruptor.workflow;

import java.util.List;

import org.springframework.integration.disruptor.workflow.DependencyGraphImpl.DependencySetter;

public interface DependencyGraph<T> extends Graph {

	List<String> getDependencies(String depender);

	List<String> getSymbolicNames();

	List<String> toSymbolicNames(Iterable<Integer> keys);

	DependencySetter addDependency(String depender);

}