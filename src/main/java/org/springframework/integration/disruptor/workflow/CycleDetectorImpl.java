package org.springframework.integration.disruptor.workflow;

public final class CycleDetectorImpl implements CycleDetector {

	public boolean hasCycle(final Graph graph) {
		final CycleDetectionContext cdc = new CycleDetectionContext(graph);
		for (int key = 0; key < graph.getSize(); key++) {
			if (cdc.wasNotVisited(key)) {
				this.visit(graph, key, cdc);
			}
		}
		return cdc.hasCycle;
	}

	private void visit(final Graph graph, final int key, final CycleDetectionContext cdc) {
		cdc.visit(key);
		cdc.putOnStack(key);
		for (final int child : graph.adjacentKeys(key)) {
			if (cdc.hasCycle) {
				return;
			} else if (cdc.wasNotVisited(child)) {
				this.visit(graph, child, cdc);
			} else if (cdc.isOnStack(child)) {
				cdc.hasCycle = true;
			}
		}
		cdc.removeFromStack(key);
	}

	private final class CycleDetectionContext extends TraversalContext {

		private final boolean onStack[];
		private boolean hasCycle = false;

		CycleDetectionContext(final Graph graph) {
			super(graph);
			this.onStack = new boolean[graph.getSize()];
		}

		void putOnStack(final int key) {
			this.onStack[key] = true;
		}

		void removeFromStack(final int key) {
			this.onStack[key] = false;
		}

		boolean isOnStack(final int key) {
			return this.onStack[key];
		}

	}

}
