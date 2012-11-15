package org.springframework.integration.disruptor.config.workflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.integration.disruptor.config.workflow.CycleDetector;
import org.springframework.integration.disruptor.config.workflow.CycleDetectorImpl;
import org.springframework.integration.disruptor.config.workflow.DependencyGraph;
import org.springframework.integration.disruptor.config.workflow.DependencyGraphImpl;

public class CycleDetectorUnitTest {

	private final CycleDetector detector = new CycleDetectorImpl();

	@Test
	public void Empty_Graph_has_no_Cycle() {
		final DependencyGraph<Object> graph = new DependencyGraphImpl<Object>();
		assertFalse(this.detector.hasCycle(graph));
	}

	@Test
	public void Graph_with_single_node_has_no_Cycle() {
		final DependencyGraph<Object> graph = new DependencyGraphImpl<Object>();
		graph.addDependency("group1").dependsOnNone();
		assertFalse(this.detector.hasCycle(graph));
	}

	@Test
	public void Graph_with_cycle_1() {
		final DependencyGraph<Object> graph = new DependencyGraphImpl<Object>();
		graph.addDependency("group1").dependsOn("group2");
		graph.addDependency("group2").dependsOn("group1");
		assertTrue(this.detector.hasCycle(graph));
	}

	@Test
	public void Graph_with_cycle_2() {
		final DependencyGraph<Object> graph = new DependencyGraphImpl<Object>();
		graph.addDependency("group1").dependsOn("group2");
		graph.addDependency("group2").dependsOn("group3");
		graph.addDependency("group3").dependsOn("group4");
		graph.addDependency("group4").dependsOn("group2");
		assertTrue(this.detector.hasCycle(graph));
	}

	@Test
	public void Graph_with_no_cycle_1() {
		final DependencyGraph<Object> graph = new DependencyGraphImpl<Object>();
		graph.addDependency("group1").dependsOn("group2");
		graph.addDependency("group2").dependsOn("group3");
		graph.addDependency("group3").dependsOn("group4");
		graph.addDependency("group4").dependsOn("group5");
		assertFalse(this.detector.hasCycle(graph));
	}

}
