package org.springframework.integration.disruptor.config.workflow.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.integration.Message;
import org.springframework.integration.disruptor.CustomEvent;
import org.springframework.integration.disruptor.config.annotation.EventTranslator;
import org.springframework.integration.support.MessageBuilder;

public class MethodInvokingMessageEventTranslatorUnitTest {

	private final SimpleTranslator simpleTranslator = new SimpleTranslator();
	private final ExtendedTranslator extendedTranslator = new ExtendedTranslator();
	private final AnnotatedTranslator annotatedTranslator = new AnnotatedTranslator();

	@Test(expected = IllegalArgumentException.class)
	public void Target_can_not_be_null() {
		new MethodInvokingMessageEventTranslator<String>(null, String.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ExpectedType_can_not_be_null() {
		new MethodInvokingMessageEventTranslator<String>(this.simpleTranslator, null);
	}

	@Test
	public void Simple_translator_method() {

		final Message<String> message = MessageBuilder.withPayload("foo payload").build();
		final CustomEvent event = new CustomEvent();

		final MethodInvokingMessageEventTranslator<CustomEvent> invoker = new MethodInvokingMessageEventTranslator<CustomEvent>(this.simpleTranslator,
				CustomEvent.class);
		invoker.translateTo(message, event);

		assertEquals("foo payload bar", event.getObject());

	}

	@Test
	public void No_suitable_translator_methods() {

		try {
			new MethodInvokingMessageEventTranslator<String>(this.simpleTranslator, String.class);
			fail("Should haved failed when no method with suitable arguments found");
		} catch (final IllegalArgumentException e) {
			assertEquals("No suitable MessageEventTranslator method was found on SimpleTranslator", e.getMessage());
		}

	}

	@Test
	public void Multiple_translator_methods() {

		try {
			new MethodInvokingMessageEventTranslator<CustomEvent>(this.extendedTranslator, CustomEvent.class);
			fail("Should haved failed when multiple suitable methods found");
		} catch (final IllegalArgumentException e) {
			assertEquals(
					"Can't decide between multiple suitable MessageEventTranslator methods: [convert, translate]; consider using @EventTranslator to designate one.",
					e.getMessage());
		}

	}

	@Test
	public void Annotated_translator_method() {

		final Message<String> message = MessageBuilder.withPayload("foo payload").build();
		final CustomEvent event = new CustomEvent();

		final MethodInvokingMessageEventTranslator<CustomEvent> invoker = new MethodInvokingMessageEventTranslator<CustomEvent>(this.annotatedTranslator,
				CustomEvent.class);
		invoker.translateTo(message, event);

		assertEquals("foo payload baz", event.getObject());

	}

	public static class SimpleTranslator {

		public void convert(final Message<?> message, final CustomEvent event) {
			final String payload = (String) message.getPayload();
			event.setObject(payload + " bar");
		}

	}

	public static class ExtendedTranslator extends SimpleTranslator {

		public void translate(final Message<?> message, final CustomEvent event) {
			final String payload = (String) message.getPayload();
			event.setObject(payload + " baz");
		}

	}

	public static class AnnotatedTranslator extends SimpleTranslator {

		@EventTranslator
		public void transform(final Message<?> message, final CustomEvent event) {
			final String payload = (String) message.getPayload();
			event.setObject(payload + " baz");
		}

	}

}
