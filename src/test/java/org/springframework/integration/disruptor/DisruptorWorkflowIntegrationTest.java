package org.springframework.integration.disruptor;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring/workflow-namespace-test-config.xml" })
public class DisruptorWorkflowIntegrationTest {

	@Autowired
	private MessageChannel channel1;

	@Autowired
	private MessageChannel channel2;

	@Test
	public void Test_Send() throws InterruptedException {
		this.channel1.send(MessageBuilder.withPayload("TEST").build());
		TimeUnit.SECONDS.sleep(30);
	}

}
