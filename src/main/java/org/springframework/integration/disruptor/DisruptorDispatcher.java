package org.springframework.integration.disruptor;

import java.util.List;

import org.springframework.integration.Message;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.dispatcher.AbstractDispatcher;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorDispatcher extends AbstractDispatcher {

	private final Disruptor<MessagingEvent> disruptor;

	public DisruptorDispatcher(final Disruptor<MessagingEvent> disruptor) {
		this.disruptor = this.registerHandlerFor(disruptor);
	}

	@SuppressWarnings("unchecked")
	private Disruptor<MessagingEvent> registerHandlerFor(final Disruptor<MessagingEvent> disruptor) {
		disruptor.handleEventsWith(new EventHandler<MessagingEvent>() {

			public void onEvent(final MessagingEvent event, final long sequence, final boolean endOfBatch) throws Exception {
				final List<MessageHandler> handlers = DisruptorDispatcher.this.getHandlers();
				for (final MessageHandler handler : handlers) {
					handler.handleMessage(event.getPayload());
				}
			}

		});
		return disruptor;
	}

	public boolean dispatch(final Message<?> message) {
		this.disruptor.publishEvent(new EventTranslator<MessagingEvent>() {

			public void translateTo(final MessagingEvent event, final long sequence) {
				event.setPayload(message);
			}

		});
		return true;
	}

	public void onInit() {
		this.disruptor.start();
	}

}
