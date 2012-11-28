package org.springframework.integration.disruptor.config.workflow;

import java.util.List;

import org.springframework.integration.disruptor.config.workflow.DependencyGraphImpl.DependencySetter;

interface DependencyGraph extends Graph {

	List<String> getDependencies(String depender);

	List<String> getSymbolicNames();

	List<String> toSymbolicNames(Iterable<Integer> keys);

	DependencySetter addDependency(String depender);

	List<String> getOrphanDependencies();

	DependencyGraph inverse();

}