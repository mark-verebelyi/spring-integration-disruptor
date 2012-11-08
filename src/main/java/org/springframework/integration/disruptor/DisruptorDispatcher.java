package org.springframework.integration.disruptor;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.integration.Message;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.dispatcher.AbstractDispatcher;
import org.springframework.integration.support.MessageBuilder;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

public class DisruptorDispatcher extends AbstractDispatcher {

	private static final class GenericEvent {

		private Object payload;

		public Object getPayload() {
			return this.payload;
		}

		public void setPayload(final Object payload) {
			this.payload = payload;
		}

		public static EventFactory<GenericEvent> newEventFactory() {
			return new EventFactory<GenericEvent>() {

				public GenericEvent newInstance() {
					return new GenericEvent();
				}

			};
		}

	}

	private final Executor executor;
	private final Disruptor<GenericEvent> disruptor;

	public DisruptorDispatcher(final int ringBufferSize) {
		this.executor = Executors.newSingleThreadExecutor();
		this.disruptor = this.newDisruptor(ringBufferSize);
		this.disruptor.start();
	}

	private Disruptor<GenericEvent> newDisruptor(final int ringBufferSize) {
		final Disruptor<GenericEvent> disruptor = new Disruptor<GenericEvent>(GenericEvent.newEventFactory(), ringBufferSize, this.executor);
		this.registerHandlers(disruptor);
		return disruptor;
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
