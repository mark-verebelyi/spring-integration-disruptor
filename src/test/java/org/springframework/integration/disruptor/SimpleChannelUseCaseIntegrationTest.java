package org.springframework.integration.disruptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring/simple-disruptor-channel-test-config.xml" })
public class SimpleChannelUseCaseIntegrationTest {

	@Autowired
	private MessageChannel channel1;

	@Test
	public void Send_Receive() {
		this.channel1.send(MessageBuilder.withPayload("TEST").build());
	}

}
