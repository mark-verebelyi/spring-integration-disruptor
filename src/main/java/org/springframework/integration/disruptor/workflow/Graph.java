package org.springframework.integration.disruptor.workflow;

import java.util.List;

public interface Graph {

	int getSize();

	List<Integer> adjacentKeys(Integer key);

}
