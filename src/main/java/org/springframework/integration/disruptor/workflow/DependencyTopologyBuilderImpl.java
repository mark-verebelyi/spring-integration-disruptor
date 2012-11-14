package org.springframework.integration.disruptor.workflow;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public final class DependencyTopologyBuilderImpl implements DependencyTopologyBuilder {

	public List<String> buildTopology(final DependencyGraph<?> graph) {
		final TopologyBuilderContext tbc = new TopologyBuilderContext(graph);
		for (int key = 0; key < graph.getSize(); key++) {
			if (tbc.wasNotVisited(key)) {
				this.visit(graph, key, tbc);
			}
		}
		return graph.toSymbolicNames(tbc.queue);
	}

	private void visit(final DependencyGraph<?> graph, final int key, final TopologyBuilderContext tbc) {
		tbc.visit(key);
		for (final int child : graph.adjacentKeys(key)) {
			if (tbc.wasNotVisited(child)) {
				this.visit(graph, child, tbc);
			}
		}
		tbc.add(key);
	}

	private static class TopologyBuilderContext extends TraversalContext {

		private final Deque<Integer> queue;

		private TopologyBuilderContext(final Graph graph) {
			super(graph);
			this.queue = new ArrayDeque<Integer>(graph.getSize());
		}

		private void add(final int key) {
			this.queue.push(key);
		}

	}

}
