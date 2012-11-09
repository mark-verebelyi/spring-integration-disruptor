package org.springframework.integration.disruptor;

import java.util.List;

import org.springframework.integration.Message;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.dispatcher.AbstractDispatcher;
import org.springframework.integration.support.MessageBuilder;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorDispatcher<T> extends AbstractDispatcher {

	private final Disruptor<T> disruptor;

	public DisruptorDispatcher(final Disruptor<T> disruptor) {
		this.disruptor = disruptor;
	}

	private Disruptor<GenericEvent> newDisruptor(final ClaimStrategy claimStrategy, final WaitStrategy waitStrategy) {
		this.registerHandlers();
	}

	@SuppressWarnings("unchecked")
	private void registerHandlers(final Disruptor<GenericEvent> disruptor) {
		disruptor.handleEventsWith(new EventHandler<GenericEvent>() {

			public void onEvent(final GenericEvent event, final long sequence, final boolean endOfBatch) throws Exception {
				final List<MessageHandler> handlers = DisruptorDispatcher.this.getHandlers();
				for (final MessageHandler handler : handlers) {
					handler.handleMessage(MessageBuilder.withPayload(event.getPayload()).build());
				}
			}

		});
	}

	public boolean dispatch(final Message<?> message) {
		this.disruptor.publishEvent(new EventTranslator<GenericEvent>() {

			public void translateTo(final GenericEvent event, final long sequence) {
				event.setPayload(message.getPayload());
			}

		});
		return true;
	}

}
