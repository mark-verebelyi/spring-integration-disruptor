package org.springframework.integration.disruptor.workflow;

import java.util.List;

public interface DependencyTopologyBuilder {

	List<String> buildTopology(DependencyGraph<?> graph);

}
