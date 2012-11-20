package org.springframework.integration.disruptor.config.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.Message;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.disruptor.DisruptorWorkflow;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.integration.disruptor.config.HandlerGroup;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;
import org.springframework.integration.disruptor.config.workflow.translator.MessagingEventTranslator;
import org.springframework.integration.disruptor.config.workflow.translator.MethodInvokingMessageEventTranslator;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public final class DisruptorWorkflowFactoryBean<T> implements FactoryBean<DisruptorWorkflow<T>>, BeanFactoryAware {

	private final Log log = LogFactory.getLog(this.getClass());

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private Set<String> publisherChannelNames;

	public void setPublisherChannelNames(final Set<String> publisherChannelNames) {
		this.publisherChannelNames = publisherChannelNames;
	}

	private String executorName;

	public void setExecutorName(final String executorName) {
		this.executorName = executorName;
	}

	private Class<T> eventType;

	public void setEventType(final Class<T> eventType) {
		this.log.info("DisruptorWorkflow is accepting the following event type '" + eventType.getName() + "'.");
		this.eventType = eventType;
	}

	private String eventFactoryName;

	public void setEventFactoryName(final String eventFactoryName) {
		this.eventFactoryName = eventFactoryName;
	}

	private String translatorName;

	public void setTranslatorName(final String translatorName) {
		this.translatorName = translatorName;
	}

	private Map<String, HandlerGroup> handlerGroups;

	public void setHandlerGroups(final Map<String, HandlerGroup> handlerGroups) {
		this.handlerGroups = handlerGroups;
	}

	private DisruptorWorkflow<T> instance;

	public DisruptorWorkflow<T> getObject() throws Exception {
		if (this.instance == null) {
			this.instance = this.createInstance();
		}
		return this.instance;
	}

	public boolean isSingleton() {
		return true;
	}

	public Class<?> getObjectType() {
		return DisruptorWorkflow.class;
	}

	private DisruptorWorkflow<T> createInstance() throws Exception {

		final RingBufferCreator<T> ringBufferCreator = new RingBufferCreator<T>(this.handlerGroups, this.eventType);
		ringBufferCreator.setBeanFactory(this.beanFactory);
		ringBufferCreator.setEventFactoryName(this.eventFactoryName);

		final EventProcessorTrackingRingBuffer<T> ringBuffer = ringBufferCreator.createRingBuffer();
		final Executor executor = this.createExecutorService(ringBuffer.getEventProcessors().size());
		final MessageEventTranslator<T> messageEventTranslator = this.createTranslator();
		final List<SubscribableChannel> subscribableChannels = this.createSubscribableChannels();

		final DisruptorWorkflow<T> instance = new DisruptorWorkflow<T>(ringBuffer.getDelegate(), executor, ringBuffer.getEventProcessors(),
				messageEventTranslator, subscribableChannels);

		return instance;

	}

	private List<SubscribableChannel> createSubscribableChannels() {
		final List<SubscribableChannel> subscribableChannels = new ArrayList<SubscribableChannel>();
		for (final String publisherChannelName : this.publisherChannelNames) {
			final SubscribableChannel subscribableChannel = this.beanFactory.getBean(publisherChannelName, SubscribableChannel.class);
			subscribableChannels.add(subscribableChannel);
		}
		return subscribableChannels;
	}

	private MessageEventTranslator<T> createTranslator() {
		if (StringUtils.hasText(this.translatorName)) {
			final Object translator = this.beanFactory.getBean(this.translatorName);
			if (this.isNativeTranslator(translator)) {
				this.log.info("'" + this.translatorName + "' is a native MessageEventTranslator.");
				@SuppressWarnings("unchecked")
				final MessageEventTranslator<T> messageToEventTranslator = (MessageEventTranslator<T>) translator;
				return messageToEventTranslator;
			} else {
				this.log.info("'" + this.translatorName + "' is not a native MessageEventTranslator, configuring MethodInvokingMessageEventTranslator.");
				return new MethodInvokingMessageEventTranslator<T>(translator, this.eventType);
			}
		} else {
			if (this.isMessagingEventType()) {
				this.log.info("'MessagingEvent' event type found, configuring default MessageEventTranslator");
				@SuppressWarnings("unchecked")
				final MessageEventTranslator<T> messagingEventTranslator = (MessageEventTranslator<T>) new MessagingEventTranslator();
				return messagingEventTranslator;
			} else {
				throw new BeanCreationException("Can't create 'workflow' without MessageEventTranslator (the one exception "
						+ "to this rule is when event type is MessagingEvent or empty)");
			}
		}
	}

	private boolean isNativeTranslator(final Object translator) {
		return (translator instanceof MessageEventTranslator)
				&& (ReflectionUtils.findMethod(translator.getClass(), "translateTo", Message.class, this.eventType) != null);
	}

	private boolean isMessagingEventType() {
		return MessagingEvent.class.isAssignableFrom(this.eventType);
	}

	private Executor createExecutorService(final int numberOfEventProcessors) {
		if (StringUtils.hasText(this.executorName)) {
			this.log.info("Configuring DisruptorWorkflow with Executor named '" + this.executorName + "'.");
			return this.beanFactory.getBean(this.executorName, Executor.class);
		} else {
			this.log.info("No bean named 'executor' has been explicitly defined. Therefore, a default Executor will be created.");
			return Executors.newCachedThreadPool();
		}
	}

}
