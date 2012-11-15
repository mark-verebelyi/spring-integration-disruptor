package org.springframework.integration.disruptor.config.workflow;

import java.util.List;

interface Graph {

	int getSize();

	List<Integer> adjacentKeys(Integer key);

}
