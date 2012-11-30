package org.springframework.integration.disruptor.config.gateway;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/workflow-gateway-test-config.xml")
public class WorkflowGatewayIntegrationTest {

	@Autowired
	private MessageEntryPoint entryPoint;

	@Test
	public void configuration_ok() {
		assertNotNull(this.entryPoint);
	}

	@Test
	public void send_simple_string_message() {
		this.entryPoint.sendMessage("Foo");
	}

}
