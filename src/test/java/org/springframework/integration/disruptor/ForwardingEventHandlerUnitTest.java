package org.springframework.integration.disruptor;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.converter.MessageConverter;
import org.springframework.integration.transformer.Transformer;

public class ForwardingEventHandlerUnitTest {

	private ForwardingEventHandler forwardingEventHandler;

	private MessageChannel channel;
	private MessageConverter converter;
	private Transformer transformer;

	@Before
	public void setup() {
		this.channel = mock(MessageChannel.class);
		this.converter = mock(MessageConverter.class);
		this.transformer = mock(Transformer.class);
		this.forwardingEventHandler = new ForwardingEventHandler();
		this.forwardingEventHandler.setChannel(this.channel);
	}

	@After
	public void teardown() {
		this.forwardingEventHandler.stop();
	}

	@Test(expected = IllegalArgumentException.class)
	public void channel_is_mandatory() {
		final ForwardingEventHandler handlerWithoutChannel = new ForwardingEventHandler();
		handlerWithoutChannel.afterPropertiesSet();
	}

	@Test
	public void string_payload_with_no_transformer_or_message_converter() throws Exception {
		this.startHandler();
		final String payload = randomAlphabetic(128);
		when(this.channel.send(payload(payload))).thenReturn(true);
		this.forwardingEventHandler.onEvent(payload, 0, false);
	}

	@Test
	public void message_payload_with_no_transformer_or_message_converter() throws Exception {
		this.startHandler();
		final Message<String> message = MessageBuilder.withPayload(randomAlphabetic(128)).build();
		when(this.channel.send(message)).thenReturn(true);
		this.forwardingEventHandler.onEvent(message, 0, false);
	}

	@Test
	public void null_payload_with_no_transformer_or_message_converter() throws Exception {
		this.startHandler();
		when(this.channel.send(null)).thenReturn(true);
		this.forwardingEventHandler.onEvent(null, 0, false);
	}

	@Test
	public void messaging_event_with_no_transformer_or_message_converter() throws Exception {
		this.startHandler();
		final Message<String> message = MessageBuilder.withPayload(randomAlphabetic(128)).build();
		final MessagingEvent event = new MessagingEvent();
		event.setPayload(message);
		when(this.channel.send(message)).thenReturn(true);
		this.forwardingEventHandler.onEvent(event, 0, false);
	}

	@Test
	public void message_payload_with_message_converter_and_no_transformer() throws Exception {
		this.forwardingEventHandler.setConverter(this.converter);
		this.startHandler();
		final Message<String> message = MessageBuilder.withPayload(randomAlphabetic(128)).build();
		when(this.channel.send(message)).thenReturn(true);
		this.forwardingEventHandler.onEvent(message, 0, false);
	}

	@Test
	public void string_payload_with_message_converter_and_no_transformer() throws Exception {
		this.forwardingEventHandler.setConverter(this.converter);
		this.startHandler();
		final String payload = randomAlphabetic(128);
		final Object converted = new Object();
		when(this.converter.toMessage(payload)).thenReturn(MessageBuilder.withPayload(converted).build());
		when(this.channel.send(payload(converted))).thenReturn(true);
		this.forwardingEventHandler.onEvent(payload, 0, false);
	}

	@Test
	public void null_payload_with_converter_and_no_transformer() throws Exception {
		this.startHandler();
		when(this.channel.send(null)).thenReturn(true);
		this.forwardingEventHandler.onEvent(null, 0, false);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void string_payload_with_converter_and_transformer() throws Exception {
		this.forwardingEventHandler.setConverter(this.converter);
		this.forwardingEventHandler.setTransformer(this.transformer);
		this.startHandler();
		final String payload = randomAlphabetic(128);
		final Message<Object> converted = MessageBuilder.withPayload(new Object()).build();
		@SuppressWarnings("rawtypes")
		final Message transformed = MessageBuilder.withPayload(new Object()).build();
		when(this.converter.toMessage(payload)).thenReturn(converted);
		when(this.transformer.transform(converted)).thenReturn(transformed);
		when(this.channel.send(transformed)).thenReturn(true);
		this.forwardingEventHandler.onEvent(payload, 0, false);
	}

	private void startHandler() {
		this.forwardingEventHandler.afterPropertiesSet();
		this.forwardingEventHandler.start();
	}

	private static Message<?> payload(final Object payload) {
		return argThat(new TypeSafeMatcher<Message<?>>() {

			public void describeTo(final Description description) {
				description.appendText("payload");
			}

			@Override
			public boolean matchesSafely(final Message<?> item) {
				return payload.equals(item.getPayload());
			}

		});
	}

}
