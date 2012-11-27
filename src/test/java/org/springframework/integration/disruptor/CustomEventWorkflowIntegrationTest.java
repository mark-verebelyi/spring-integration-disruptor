package org.springframework.integration.disruptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring/workflow-custom-event-test-config.xml" })
public class CustomEventWorkflowIntegrationTest {

	@Autowired
	private MessageChannel channel;

	@Test
	public void try_custom_event() {
		this.channel.send(MessageBuilder.withPayload("Foo").build());
	}

}
