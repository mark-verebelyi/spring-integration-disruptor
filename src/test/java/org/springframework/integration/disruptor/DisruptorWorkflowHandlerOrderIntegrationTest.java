package org.springframework.integration.disruptor;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/spring/workflow-handler-order-test-config.xml" })
public class DisruptorWorkflowHandlerOrderIntegrationTest {

	private static int REPEAT_COUNT = 100;

	@Autowired
	private SubscribableChannel channel1;

	@Autowired
	private HandlerOrderTracker tracker;

	@Test
	public void First() throws InterruptedException {

		final List<String> handlerNames = Arrays.asList("handler1", "handler2", "handler3", "handler4", "handler5", "handler6");

		for (int i = 0; i < REPEAT_COUNT; i++) {

			this.channel1.send(MessageBuilder.withPayload("Foo").build());

			this.tracker.waitFor(handlerNames.size());

			final List<String> handlerNamesInOrder = this.tracker.getHandlerNamesInOrder();

			assertTrue(handlerNamesInOrder.containsAll(handlerNames));
			assertBefore(handlerNamesInOrder, "handler1", Arrays.asList("handler4", "handler5", "handler6"));
			assertBefore(handlerNamesInOrder, "handler2", Arrays.asList("handler4", "handler5", "handler6"));
			assertBefore(handlerNamesInOrder, "handler3", Arrays.asList("handler4", "handler5", "handler6"));

			this.tracker.clearHandlerNames();

		}

	}

	private static void assertBefore(final List<String> handlerNamesInOrder, final String handler, final List<String> handlersAfter) {
		final int handlerIndex = handlerNamesInOrder.indexOf(handler);
		assertTrue("No such handler: " + handler + "; " + handlerNamesInOrder.toString(), handlerIndex > -1);
		for (final String handlerAfter : handlersAfter) {
			final int handlerAfterIndex = handlerNamesInOrder.indexOf(handlerAfter);
			assertTrue("No such handler: " + handlerAfter + "; " + handlerNamesInOrder.toString(), handlerIndex > -1);
			assertTrue(handler + " should preceed " + handlerAfter, handlerIndex < handlerAfterIndex);
		}
	}
}
