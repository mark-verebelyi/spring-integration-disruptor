package org.springframework.integration.disruptor;

import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.support.converter.MessageConverter;
import org.springframework.integration.support.converter.SimpleMessageConverter;
import org.springframework.integration.transformer.Transformer;
import org.springframework.util.Assert;

import com.lmax.disruptor.EventHandler;

/**
 * An {@link EventHandler} that can forward the event to a
 * {@link MessageChannel}.
 */
public final class ForwardingEventHandler extends AbstractEndpoint implements EventHandler<Object> {

	private MessageChannel channel;

	/**
	 * The {@link MessageChannel} where the {@link Message}s get forwarded.
	 */
	public void setChannel(final MessageChannel channel) {
		this.channel = channel;
	}

	private Transformer transformer;

	/**
	 * A {@link Transformer} which can transform the handled message type to the
	 * designated message channel's type. If left null, then the message is
	 * forwarded as is.
	 */
	public void setTransformer(final Transformer transformer) {
		this.transformer = transformer;
	}

	private MessageConverter converter;

	/**
	 * {@link MessageConverter} that is used to transform the event to the
	 * designated channel's type. If left null, then the
	 */
	public void setConverter(final MessageConverter converter) {
		this.converter = converter;
	}

	private MessagingTemplate messagingTemplate = new MessagingTemplate();

	/**
	 * {@link MessagingTemplate} used for sending messages to the configured
	 * channel.
	 */
	public void setMessagingTemplate(final MessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public void onEvent(final Object event, final long sequence, final boolean endOfBatch) throws Exception {
		final Message<?> message = this.transform(this.convert(event));
		this.messagingTemplate.send(message);
	}

	private Message<?> transform(final Message<?> message) {
		if (message == null) {
			return null;
		}
		return (this.transformer != null ? this.transformer.transform(message) : message);
	}

	private Message<?> convert(final Object event) throws Exception {
		if (event == null) {
			return null;
		} else if (event instanceof Message) {
			return (Message<?>) event;
		} else if (event instanceof MessagingEvent) {
			return ((MessagingEvent) event).getPayload();
		} else {
			return this.converter.toMessage(event);
		}
	}

	@Override
	protected void onInit() throws Exception {
		Assert.isTrue(this.channel != null, "'channel' attribtue is mandatory.");
		this.messagingTemplate.setDefaultChannel(this.channel);
		if (this.converter == null) {
			this.converter = new SimpleMessageConverter();
		}
	}

	@Override
	protected void doStart() {
	}

	@Override
	protected void doStop() {
	}

}
