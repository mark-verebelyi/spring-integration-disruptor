package org.springframework.integration.disruptor;

public class CustomEventHandler {

	public void handle(final CustomEvent event) {
		System.out.println(event.getObject());
	}

}
