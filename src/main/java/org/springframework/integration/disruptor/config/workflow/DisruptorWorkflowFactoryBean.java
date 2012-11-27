package org.springframework.integration.disruptor.config.workflow;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.disruptor.MessageDrivenDisruptorWorkflow;
import org.springframework.integration.disruptor.config.HandlerGroup;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;

import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.WaitStrategy;

public final class DisruptorWorkflowFactoryBean<T> implements FactoryBean<MessageDrivenDisruptorWorkflow<T>>, BeanFactoryAware, SmartLifecycle {

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

	private WaitStrategy waitStrategy;

	public void setWaitStrategy(final WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	private ClaimStrategy claimStrategy;

	public void setClaimStrategy(final ClaimStrategy claimStrategy) {
		this.claimStrategy = claimStrategy;
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

	public int getPhase() {
		return this.instance != null ? this.instance.getPhase() : 0;
	}

	public boolean isRunning() {
		return this.instance != null ? this.instance.isRunning() : false;
	}

	public void start() {
		if (this.instance == null) {
			this.instance = this.createInstance();
		}
		if (this.isStopped()) {
			this.instance.start();
		}
	}

	private boolean isStopped() {
		return !this.isRunning();
	}

	public void stop() {
		if ((this.instance != null) && this.isRunning()) {
			this.instance.stop();
		}
	}

	public boolean isAutoStartup() {
		return this.instance != null ? this.instance.isAutoStartup() : true;
	}

	public void stop(final Runnable callback) {
		if ((this.instance != null) && this.isRunning()) {
			this.instance.stop(callback);
		}
	}

	private static void initialize(final Object object) {
		try {
			if (object instanceof InitializingBean) {
				((InitializingBean) object).afterPropertiesSet();
			}
		} catch (final Exception e) {
			throw new BeanCreationException("Exception while initializing: " + object, e);
		}
	}

	private MessageDrivenDisruptorWorkflow<T> createInstance() {

		final RingBufferFactory<T> ringBufferFactory = new RingBufferFactory<T>();
		ringBufferFactory.setBeanFactory(this.beanFactory);
		ringBufferFactory.setEventFactoryName(this.eventFactoryName);
		ringBufferFactory.setEventType(this.eventType);
		ringBufferFactory.setHandlerGroups(this.handlerGroups);
		ringBufferFactory.setWaitStrategy(this.waitStrategy);
		ringBufferFactory.setClaimStrategy(this.claimStrategy);
		initialize(ringBufferFactory);

		final ExecutorServiceFactory executorServiceFactory = new ExecutorServiceFactory();
		executorServiceFactory.setBeanFactory(this.beanFactory);
		executorServiceFactory.setExecutorName(this.executorName);
		initialize(executorServiceFactory);

		final MessageEventTranslatorFactory<T> messageEventTranslatorFactory = new MessageEventTranslatorFactory<T>();
		messageEventTranslatorFactory.setBeanFactory(this.beanFactory);
		messageEventTranslatorFactory.setEventType(this.eventType);
		messageEventTranslatorFactory.setTranslatorName(this.translatorName);
		initialize(messageEventTranslatorFactory);

		final SubscribableChannelFactory subscribableChannelFactory = new SubscribableChannelFactory();
		subscribableChannelFactory.setBeanFactory(this.beanFactory);
		subscribableChannelFactory.setPublisherChannelNames(this.publisherChannelNames);
		initialize(subscribableChannelFactory);

		final EventProcessorTrackingRingBuffer<T> ringBuffer = ringBufferFactory.createRingBuffer();
		final Executor executor = executorServiceFactory.createExecutorService();
		final MessageEventTranslator<T> messageEventTranslator = messageEventTranslatorFactory.createTranslator();
		final List<SubscribableChannel> subscribableChannels = subscribableChannelFactory.createSubscribableChannels();

		return new MessageDrivenDisruptorWorkflow<T>(ringBuffer.getDelegate(), executor, ringBuffer.getEventProcessors(), messageEventTranslator, subscribableChannels);

	}

}
