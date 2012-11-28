package org.springframework.integration.disruptor.config.workflow;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.SmartLifecycle;
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

	private String translatorName;

	public void setTranslatorName(final String translatorName) {
		this.translatorName = translatorName;
	}

	private MessageDrivenDisruptorWorkflow<T> instance;

	public MessageDrivenDisruptorWorkflow<T> getObject() throws Exception {
		return this.instance;
	}

	public boolean isSingleton() {
		return true;
	}

	public Class<?> getObjectType() {
		return MessageDrivenDisruptorWorkflow.class;
	}

	@Override
	protected void createInstance() {

		final RingBuffer<T> ringBuffer = this.createRingBuffer();
		final Executor executor = this.createExecutor();

		final MessageEventTranslatorFactory<T> messageEventTranslatorFactory = new MessageEventTranslatorFactory<T>();
		messageEventTranslatorFactory.setBeanFactory(this.beanFactory);
		messageEventTranslatorFactory.setEventType(this.eventType);
		messageEventTranslatorFactory.setTranslatorName(this.translatorName);
		initialize(messageEventTranslatorFactory);

		final SubscribableChannelFactory subscribableChannelFactory = new SubscribableChannelFactory();
		subscribableChannelFactory.setBeanFactory(this.beanFactory);
		subscribableChannelFactory.setPublisherChannelNames(this.publisherChannelNames);
		initialize(subscribableChannelFactory);

		final MessageEventTranslator<T> messageEventTranslator = messageEventTranslatorFactory.createTranslator();
		final List<SubscribableChannel> subscribableChannels = subscribableChannelFactory.createSubscribableChannels();
		final List<EventProcessor> eventProcessors = this.handlerGroupDefinition.getAllEventProcessors();

		this.instance = new MessageDrivenDisruptorWorkflow<T>(ringBuffer, executor, eventProcessors, messageEventTranslator, subscribableChannels);

	}

	@Override
	protected SmartLifecycle getInstance() {
		return this.instance;
	}
}
