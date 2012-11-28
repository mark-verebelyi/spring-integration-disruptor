package org.springframework.integration.disruptor.config.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class DependencyGraphUnitTest {

	@Test
	public void DependsOnNone_1() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOnNone();
		assertEquals("Size <> 1", 1, graph.getSize());
		assertEquals("Dependencies Size <> 0", 0, graph.getDependencies("group1").size());
		assertTrue("Wrong dependencies", graph.getDependencies("group1").isEmpty());
	}

	@Test
	public void DependsOnNone_2() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOnNone();
		graph.addDependency("group2").dependsOn("group1");
		assertEquals("Size <> 2", 2, graph.getSize());
		assertEquals("Dependencies Size <> 1", 1, graph.getDependencies("group2").size());
		assertTrue("Wrong dependencies", graph.getDependencies("group2").contains("group1"));
	}

	@Test
	public void Add_Dependency_1() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOn("group2");
		assertEquals("Size <> 2", 2, graph.getSize());
		assertEquals("Dependencies Size <> 1", 1, graph.getDependencies("group1").size());
		assertTrue("Wrong dependencies", graph.getDependencies("group1").contains("group2"));
	}

	@Test
	public void Add_Dependency_2() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOn("group2");
		graph.addDependency("group1").dependsOn("group3");
		graph.addDependency("group1").dependsOn("group4");
		assertEquals("Size <> 4", 4, graph.getSize());
		assertEquals("Dependencies Size <> 3", 3, graph.getDependencies("group1").size());
		assertTrue("Wrong dependencies", graph.getDependencies("group1").containsAll(Arrays.asList("group2", "group3", "group4")));
	}

	@Test
	public void Add_Dependency_3() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOn("group2");
		graph.addDependency("group2").dependsOn("group3");
		graph.addDependency("group3").dependsOn("group4");
		assertEquals("Size <> 4", 4, graph.getSize());
		assertEquals("Dependencies Size <> 1 Group 1", 1, graph.getDependencies("group1").size());
		assertEquals("Dependencies Size <> 1 Group 2", 1, graph.getDependencies("group2").size());
		assertEquals("Dependencies Size <> 1 Group 3", 1, graph.getDependencies("group3").size());
		assertEquals("Dependencies Size <> 0 Group 4", 0, graph.getDependencies("group4").size());
		assertTrue("Wrong dependencies Group 1", graph.getDependencies("group1").contains("group2"));
		assertTrue("Wrong dependencies Group 2", graph.getDependencies("group2").contains("group3"));
		assertTrue("Wrong dependencies Group 3", graph.getDependencies("group3").contains("group4"));
		assertTrue("Wrong dependencies Group 4", graph.getDependencies("group4").isEmpty());
	}

	@Test
	public void Orphan_Dependencies_1() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOn("group2");
		assertEquals("Orphan Dependencies Size <> 1", 1, graph.getOrphanDependencies().size());
		assertTrue("Wrong orphan dependencies", graph.getOrphanDependencies().contains("group2"));
	}

	@Test
	public void Orphan_Dependencies_2() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOn("group2");
		graph.addDependency("group2").dependsOn("group3");
		graph.addDependency("group4").dependsOn("group3");
		graph.addDependency("group5").dependsOn("group6");
		assertEquals("Orphan Dependencies Size <> 2", 2, graph.getOrphanDependencies().size());
		assertTrue("Wrong orphan dependencies", graph.getOrphanDependencies().containsAll(Arrays.asList("group3", "group6")));
	}

	@Test
	public void Inverse_1() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOn("group2");
		final DependencyGraph inverse = graph.inverse();
		assertEquals("Size <> 2", 2, inverse.getSize());
		assertEquals("Dependencies Size <> 1 Group 2", 1, inverse.getDependencies("group2").size());
		assertTrue("Wrong dependencies Group 2", inverse.getDependencies("group2").contains("group1"));
		assertEquals("Dependencies Size <> 0 Group 1", 0, inverse.getDependencies("group1").size());
		assertTrue("Wrong dependencies Group 1", inverse.getDependencies("group1").isEmpty());
	}

	@Test
	public void GetSymbolicNames_1() {
		final DependencyGraph graph = new DependencyGraphImpl();
		graph.addDependency("group1").dependsOnNone();
		graph.addDependency("group2").dependsOn("group1");
		graph.addDependency("group3").dependsOn("group4");
		graph.addDependency("group5").dependsOnNone();
		assertEquals("Symbolic Names Size <> 5", 5, graph.getSymbolicNames().size());
		assertTrue("Wrong Symbolic Names", graph.getSymbolicNames().containsAll(Arrays.asList("group1", "group2", "group3", "group4", "group5")));

	}

}
