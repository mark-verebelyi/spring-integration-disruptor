package org.springframework.integration.disruptor.config.workflow;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.springframework.integration.disruptor.config.workflow.CycleDetector;
import org.springframework.integration.disruptor.config.workflow.CycleDetectorImpl;
import org.springframework.integration.disruptor.config.workflow.DependencyGraph;
import org.springframework.integration.disruptor.config.workflow.DependencyGraphImpl;
import org.springframework.integration.disruptor.config.workflow.DependencyTopologyBuilder;
import org.springframework.integration.disruptor.config.workflow.DependencyTopologyBuilderImpl;

public class Stonyci {

	private final CycleDetector cycleDetector = new CycleDetectorImpl();

	private final DependencyTopologyBuilder topologyBuilder = new DependencyTopologyBuilderImpl();

	@Test
	public void stony() {

		final DependencyGraph<Object> graph = new DependencyGraphImpl<Object>();

		graph.addDependency("1").dependsOn("ring-buffer");
		graph.addDependency("2").dependsOn("ring-buffer");
		graph.addDependency("3").dependsOn("ring-buffer");

		graph.addDependency("4").dependsOn("1");
		graph.addDependency("4").dependsOn("2");

		graph.addDependency("5").dependsOn("3");

		graph.addDependency("6").dependsOn("4");

		graph.addDependency("7").dependsOn("4");
		graph.addDependency("7").dependsOn("5");

		graph.addDependency("8").dependsOn("5");

		assertFalse(this.cycleDetector.hasCycle(graph));

		final List<String> topology = this.topologyBuilder.buildTopology(graph);
		for (final String element : topology) {
			System.out.println(element);
		}
		System.out.println("Orphans> " + graph.getOrphanDependencies());
		System.out.println("---");

		final DependencyGraph<Object> inverseGraph = graph.inverse();
		final List<String> inverseTopology = this.topologyBuilder.buildTopology(inverseGraph);
		for (final String element : inverseTopology) {
			System.out.println(element + "> " + graph.getDependencies(element));
		}
		System.out.println("Orphans> " + inverseGraph.getOrphanDependencies());

	}
}
