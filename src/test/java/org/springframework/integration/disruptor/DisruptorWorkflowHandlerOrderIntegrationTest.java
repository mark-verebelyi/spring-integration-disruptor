package org.springframework.integration.disruptor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private HandlerOrderTracker tracker1;

	@Autowired
	private SubscribableChannel channel2;

	@Autowired
	private HandlerOrderTracker tracker2;

	static final String HANDLER_1 = "handler1";
	static final String HANDLER_2 = "handler2";
	static final String HANDLER_3 = "handler3";
	static final String HANDLER_4 = "handler4";
	static final String HANDLER_5 = "handler5";
	static final String HANDLER_6 = "handler6";

	static final List<String> GROUP_1 = Arrays.asList(HANDLER_1, HANDLER_2);
	static final List<String> GROUP_2 = Arrays.asList(HANDLER_3);
	static final List<String> GROUP_3 = Arrays.asList(HANDLER_4, HANDLER_5, HANDLER_6);

	@SuppressWarnings("unchecked")
	@Test
	public void Simple_example() throws InterruptedException {

		final List<String> handlerNames = Arrays.asList(HANDLER_1, HANDLER_2, HANDLER_3, HANDLER_4, HANDLER_5, HANDLER_6);

		for (int i = 0; i < REPEAT_COUNT; i++) {

			this.channel1.send(MessageBuilder.withPayload("Foo").build());
			this.tracker1.waitFor(handlerNames.size());

			final List<String> handlerNamesInOrder = this.tracker1.getHandlerNamesInOrder();
			assertTrue(handlerNamesInOrder.containsAll(handlerNames));

			final OrderAsserter asserter = with(handlerNamesInOrder);
			asserter.assertThat(HANDLER_1).isBefore(GROUP_3);
			asserter.assertThat(HANDLER_2).isBefore(GROUP_3);
			asserter.assertThat(HANDLER_3).isBefore(GROUP_3);

			this.tracker1.clearHandlerNames();

		}

	}

	static final String ENGINE = "engineHandler";
	static final String DRIVER_SEAT = "driverSeatHandler";
	static final String PASSENGER_SEAT = "passengerSeatHandler";
	static final String REAR_SEAT = "rearSeatHandler";
	static final String BONET = "bonetHandler";
	static final String FRONT_DOOR_TWO = "frontDoorTwoHandler";
	static final String FRONT_DOOR_ONE = "frontDoorOneHandler";
	static final String REAR_DOOR_ONE = "rearDoorOneHandler";
	static final String REAR_DOOR_TWO = "rearDoorTwoHandler";
	static final String PAINT = "paintHandler";
	static final String WHEEL_ONE = "wheelOneHandler";
	static final String WHEEL_TWO = "wheelTwoHandler";
	static final String WHEEL_THREE = "wheelThreeHandler";
	static final String WHEEL_FOUR = "wheelFourHandler";

	static final List<String> ENGINE_GROUP = Arrays.asList(ENGINE);
	static final List<String> SEAT_GROUP = Arrays.asList(DRIVER_SEAT, PASSENGER_SEAT, REAR_SEAT);
	static final List<String> BONET_GROUP = Arrays.asList(BONET);
	static final List<String> DOOR_GROUP = Arrays.asList(FRONT_DOOR_ONE, FRONT_DOOR_TWO, REAR_DOOR_ONE, REAR_DOOR_TWO);
	static final List<String> PAINT_GROUP = Arrays.asList(PAINT);
	static final List<String> WHEEL_GROUP = Arrays.asList(WHEEL_ONE, WHEEL_TWO, WHEEL_THREE, WHEEL_FOUR);

	@SuppressWarnings("unchecked")
	@Test
	public void InfoQ_presentation_example() throws InterruptedException {

		final List<String> handlerNames = Arrays.asList(ENGINE, DRIVER_SEAT, PASSENGER_SEAT, REAR_SEAT, BONET, FRONT_DOOR_ONE, FRONT_DOOR_TWO, REAR_DOOR_ONE,
				REAR_DOOR_TWO, PAINT, WHEEL_ONE, WHEEL_TWO, WHEEL_THREE, WHEEL_FOUR);

		for (int i = 0; i < REPEAT_COUNT; i++) {

			this.channel2.send(MessageBuilder.withPayload("Foo").build());
			this.tracker2.waitFor(handlerNames.size());

			final List<String> handlerNamesInOrder = this.tracker2.getHandlerNamesInOrder();
			assertTrue(handlerNamesInOrder.containsAll(handlerNames));

			final OrderAsserter asserter = with(handlerNamesInOrder);
			asserter.assertThat(ENGINE).isBefore(BONET_GROUP, PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(DRIVER_SEAT).isBefore(DOOR_GROUP, PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(PASSENGER_SEAT).isBefore(DOOR_GROUP, PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(REAR_SEAT).isBefore(DOOR_GROUP, PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(BONET).isBefore(PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(FRONT_DOOR_ONE).isBefore(PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(FRONT_DOOR_TWO).isBefore(PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(REAR_DOOR_ONE).isBefore(PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(REAR_DOOR_TWO).isBefore(PAINT_GROUP, WHEEL_GROUP);
			asserter.assertThat(PAINT).isBefore(WHEEL_GROUP);

			this.tracker2.clearHandlerNames();

		}

	}

	public static OrderAsserter with(final List<String> handlerNamesInOrder) {
		return new OrderAsserter(handlerNamesInOrder);
	}

	private static class OrderAsserter {

		private final Map<String, Integer> handlerNameMap = new HashMap<String, Integer>();
		private String handler;

		private OrderAsserter(final List<String> handlerNamesInOrder) {
			for (int i = 0; i < handlerNamesInOrder.size(); i++) {
				this.handlerNameMap.put(handlerNamesInOrder.get(i), i);
			}
		}

		public void isBefore(final List<String>... groups) {
			final List<String> handlersAfter = new ArrayList<String>();
			for (final List<String> group : groups) {
				handlersAfter.addAll(group);
			}
			assertBefore(this.handlerNameMap, this.handler, handlersAfter);

		}

		public OrderAsserter assertThat(final String handler) {
			this.handler = handler;
			return this;
		}

		private static void assertBefore(final Map<String, Integer> handlerNameMap, final String handler, final List<String> handlersAfter) {
			final Integer handlerIndex = handlerNameMap.get(handler);
			assertNotNull("No such handler: " + handler + "; " + handlerNameMap.toString(), handlerIndex);
			for (final String handlerAfter : handlersAfter) {
				final int handlerAfterIndex = handlerNameMap.get(handlerAfter);
				assertNotNull("No such handler: " + handlerAfter + "; " + handlerNameMap.toString(), handlerIndex);
				assertTrue(handler + " should preceed " + handlerAfter, handlerIndex < handlerAfterIndex);
			}
		}

	}

}
