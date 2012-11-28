package org.springframework.integration.disruptor.config.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class DependencyTopologyBuilderUnitTest {

	private final DependencyTopologyBuilder topologyBuilder = new DependencyTopologyBuilderImpl();

	@Test
	public void Empty_Graph() {
		final DependencyGraph graph = new DependencyGraphImpl();
		assertNotNull(this.topologyBuilder.buildTopology(graph));
		assertTrue(this.topologyBuilder.buildTopology(graph).isEmpty());
	}

	@Test
	public void Graph_with_single_node() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOnNone();
		final List<String> topology = this.topologyBuilder.buildTopology(graph);
		assertNotNull(topology);
		assertEquals(1, topology.size());
		assertEquals("group1", topology.get(0));
	}

	@Test
	public void Graph_with_multiple_nodes_1() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group2").dependsOn("group1");
		graph.addDependency("group3").dependsOn("group1");
		final List<String> topology = this.topologyBuilder.buildTopology(graph);
		assertNotNull(topology);
		assertEquals(3, topology.size());
		assertEquals("group3", topology.get(0));
		assertEquals("group2", topology.get(1));
		assertEquals("group1", topology.get(2));
	}

	@Test
	public void Graph_with_multiple_nodes_2() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group2").dependsOn("group1");
		graph.addDependency("group3").dependsOn("group2");
		graph.addDependency("group1").dependsOn("group4");
		final List<String> topology = this.topologyBuilder.buildTopology(graph);
		assertNotNull(topology);
		assertEquals(4, topology.size());
		assertEquals("group3", topology.get(0));
		assertEquals("group2", topology.get(1));
		assertEquals("group1", topology.get(2));
		assertEquals("group4", topology.get(3));
	}
}
