package org.springframework.integration.disruptor.workflow;

abstract class TraversalContext {

	private final boolean marked[];

	TraversalContext(final Graph graph) {
		this.marked = new boolean[graph.getSize()];
	}

	protected void visit(final int key) {
		this.marked[key] = true;
	}

	protected boolean wasNotVisited(final int key) {
		return !this.marked[key];
	}

}