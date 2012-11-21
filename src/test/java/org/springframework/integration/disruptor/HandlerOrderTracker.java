package org.springframework.integration.disruptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.util.Assert;

public final class HandlerOrderTracker {

	private final List<String> handlerNamesInOrder = Collections.synchronizedList(new ArrayList<String>());

	public void addHandlerName(final String name) {
		Assert.isTrue(name != null, "Name can not be null");
		this.handlerNamesInOrder.add(name);
	}

	public List<String> getHandlerNamesInOrder() {
		return Collections.unmodifiableList(this.handlerNamesInOrder);
	}

	public void clearHandlerNames() {
		this.handlerNamesInOrder.clear();
	}

	public void waitFor(final int numberOfHandlers) throws InterruptedException {
		while (this.handlerNamesInOrder.size() < numberOfHandlers) {
			TimeUnit.MILLISECONDS.sleep(100);
		}
	}

}
