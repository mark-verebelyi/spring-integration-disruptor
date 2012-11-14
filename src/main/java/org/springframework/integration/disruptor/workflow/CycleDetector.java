package org.springframework.integration.disruptor.workflow;

public interface CycleDetector {

	boolean hasCycle(Graph graph);

}