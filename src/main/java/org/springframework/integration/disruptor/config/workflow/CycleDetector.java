package org.springframework.integration.disruptor.config.workflow;

interface CycleDetector {

	boolean hasCycle(Graph graph);

}