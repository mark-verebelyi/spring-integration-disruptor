package org.springframework.integration.disruptor.config.workflow;

import java.util.List;

interface DependencyTopologyBuilder {

	List<String> buildTopology(DependencyGraph graph);

}
