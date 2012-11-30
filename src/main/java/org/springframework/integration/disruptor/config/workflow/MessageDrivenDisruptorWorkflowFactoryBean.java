package org.springframework.integration.disruptor.config.workflow;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.disruptor.MessageDrivenDisruptorWorkflow;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;

public final class MessageDrivenDisruptorWorkflowFactoryBean<T> extends AbstractDisruptorWorkflowFactoryBean<T> implements
		FactoryBean<MessageDrivenDisruptorWorkflow<T>> {

	private Set<String> publisherChannelNames;

	public void setPublisherChannelNames(final Set<String> publisherChannelNames) {
		this.publisherChannelNames = publisherChannelNames;
	}

	public MessageDrivenDisruptorWorkflow<T> getObject() throws Exception {
		return (MessageDrivenDisruptorWorkflow<T>) this.getInstance();
	}

	public boolean isSingleton() {
		return true;
	}

	public Class<?> getObjectType() {
		return MessageDrivenDisruptorWorkflow.class;
	}

	@Override
	protected MessageDrivenDisruptorWorkflow<T> createInstance(final RingBuffer<T> ringBuffer, final Executor executor,
			final List<EventProcessor> eventProcessors, final MessageEventTranslator<T> messageEventTranslator) {

		final SubscribableChannelFactory subscribableChannelFactory = new SubscribableChannelFactory();
		subscribableChannelFactory.setBeanFactory(this.beanFactory);
		subscribableChannelFactory.setPublisherChannelNames(this.publisherChannelNames);
		initialize(subscribableChannelFactory);

		final List<SubscribableChannel> subscribableChannels = subscribableChannelFactory.createSubscribableChannels();

		return new MessageDrivenDisruptorWorkflow<T>(ringBuffer, executor, eventProcessors, messageEventTranslator, subscribableChannels);

	}

}
