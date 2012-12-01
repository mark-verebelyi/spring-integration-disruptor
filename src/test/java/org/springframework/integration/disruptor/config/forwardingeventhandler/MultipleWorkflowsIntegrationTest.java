package org.springframework.integration.disruptor.config.forwardingeventhandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.disruptor.DisruptorWorkflow;
import org.springframework.integration.disruptor.HandlerOrderTracker;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/multiple-workflows-connected-test-config.xml")
public class MultipleWorkflowsIntegrationTest {

	@Autowired
	private HandlerOrderTracker tracker;

	@Autowired
	private DisruptorWorkflow<Message<?>> source;

	@Test
	public void config_ok() throws InterruptedException {
		this.source.send(MessageBuilder.withPayload("foo").build());
		this.tracker.waitFor(1);
	}

}
