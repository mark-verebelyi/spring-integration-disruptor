package org.springframework.integration.disruptor.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.disruptor.DisruptorWorkflow;
import org.springframework.integration.endpoint.EventDrivenConsumer;

public class DisruptorWorkflowFactoryBean<Event> implements FactoryBean<DisruptorWorkflow<Event>>, BeanFactoryAware, SmartLifecycle, InitializingBean {

	private DisruptorWorkflow<Event> disruptorWorkflow;

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private Set<String> publisherChannelNames;

	public void setPublisherChannelNames(final Set<String> publisherChannelNames) {
		this.publisherChannelNames = publisherChannelNames;
	}

	private final List<EventDrivenConsumer> consumers = new ArrayList<EventDrivenConsumer>();

	public DisruptorWorkflow<Event> getObject() throws Exception {
		System.out.println("DisruptorWorkflowFactoryBean.getObject()");
		return this.disruptorWorkflow;
	}

	public void afterPropertiesSet() throws Exception {
		System.out.println("DisruptorWorkflowFactoryBean.afterPropertiesSet()");
		if (this.disruptorWorkflow == null) {
			this.disruptorWorkflow = new DisruptorWorkflow<Event>();
		}
		this.registerEventDrivenConstumers();
	}

	private void registerEventDrivenConstumers() {
		for (final String publisherChannelName : this.publisherChannelNames) {
			final SubscribableChannel subscribableChannel = this.beanFactory.getBean(publisherChannelName, SubscribableChannel.class);
			this.consumers.add(new EventDrivenConsumer(subscribableChannel, this.disruptorWorkflow));
		}
	}

	public Class<?> getObjectType() {
		System.out.println("DisruptorWorkflowFactoryBean.getObjectType()");
		return DisruptorWorkflow.class;
	}

	public boolean isSingleton() {
		System.out.println("DisruptorWorkflowFactoryBean.isSingleton()");
		return true;
	}

	public void start() {
		System.out.println("DisruptorWorkflowFactoryBean.start()");
		for (final EventDrivenConsumer consumer : this.consumers) {
			consumer.start();
		}
		this.disruptorWorkflow.start();
	}

	public void stop() {
		System.out.println("DisruptorWorkflowFactoryBean.stop()");
		for (final EventDrivenConsumer consumer : this.consumers) {
			consumer.stop();
		}
		this.disruptorWorkflow.stop();
	}

	public boolean isRunning() {
		System.out.println("DisruptorWorkflowFactoryBean.isRunning()");
		return (this.disruptorWorkflow != null) && this.disruptorWorkflow.isRunning();
	}

	public int getPhase() {
		System.out.println("DisruptorWorkflowFactoryBean.getPhase()");
		return Integer.MIN_VALUE;
	}

	public boolean isAutoStartup() {
		return (this.disruptorWorkflow != null) && this.disruptorWorkflow.isAutoStartup();
	}

	public void stop(final Runnable callback) {
		this.stop();
	}

}
