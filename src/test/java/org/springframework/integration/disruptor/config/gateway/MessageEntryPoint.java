package org.springframework.integration.disruptor.config.gateway;

public interface MessageEntryPoint {

	void sendMessage(String message);

}
