package org.springframework.integration.disruptor.config.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.disruptor.DisruptorWorkflow;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.integration.disruptor.config.HandlerGroup;
import org.springframework.integration.endpoint.EventDrivenConsumer;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;

public final class DisruptorWorkflowFactoryBean implements FactoryBean<DisruptorWorkflow>, BeanFactoryAware, SmartLifecycle, InitializingBean {

	private DisruptorWorkflow disruptorWorkflow;

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private Set<String> publisherChannelNames;

	public void setPublisherChannelNames(final Set<String> publisherChannelNames) {
		this.publisherChannelNames = publisherChannelNames;
	}

	private Map<String, HandlerGroup> handlerGroups = new HashMap<String, HandlerGroup>();

	public void setHandlerGroups(final Map<String, HandlerGroup> handlerGroups) {
		this.handlerGroups = handlerGroups;
	}

	private final List<EventDrivenConsumer> consumers = new ArrayList<EventDrivenConsumer>();

	public DisruptorWorkflow getObject() throws Exception {
		System.out.println("DisruptorWorkflowFactoryBean.getObject()");
		return this.disruptorWorkflow;
	}

	public void afterPropertiesSet() throws Exception {

		final ExecutorService executor = Executors.newCachedThreadPool();

		System.out.println("DisruptorWorkflowFactoryBean.afterPropertiesSet()");

		final RingBuffer<MessagingEvent> ringBuffer = this.createRingBuffer();
		final SequenceBarrier ringBufferBarrier = ringBuffer.newBarrier();

		final DependencyGraph<List<? extends EventProcessor>> dependencyGraph = DependencyGraphImpl.forHandlerGroups(this.handlerGroups.values());

		final CycleDetector cycleDetector = new CycleDetectorImpl();
		if (cycleDetector.hasCycle(dependencyGraph)) {
			throw new BeanCreationException("Circular 'handler-group' dependency detected while creating DisruptorWorkflow");
		}

		final DependencyGraph<List<? extends EventProcessor>> inverseDependencyGraph = dependencyGraph.inverse();

		final DependencyTopologyBuilder topologyBuilder = new DependencyTopologyBuilderImpl();

		final List<String> handlerGroupNames = topologyBuilder.buildTopology(inverseDependencyGraph);

		for (final String handlerGroupName : handlerGroupNames) {
			if ("ring-buffer".equals(handlerGroupName)) {
				continue;
			}

			final HandlerGroup handlerGroup = this.handlerGroups.get(handlerGroupName);
			if (handlerGroup.hasSingleDependency("ring-buffer")) {
				final List<EventHandler<MessagingEvent>> eventHandlers = this.getEventHandlers(handlerGroup);
				final List<BatchEventProcessor<MessagingEvent>> batchEventProcessors = new ArrayList<BatchEventProcessor<MessagingEvent>>();
				for (final EventHandler<MessagingEvent> eventHandler : eventHandlers) {
					final BatchEventProcessor<MessagingEvent> batchEventProcessor = new BatchEventProcessor<MessagingEvent>(ringBuffer, ringBufferBarrier,
							eventHandler);
					executor.submit(batchEventProcessor);
					batchEventProcessors.add(batchEventProcessor);
				}
				dependencyGraph.putData(handlerGroupName, batchEventProcessors);
			} else {
				final Sequence[] barriers = toArray(this.findBarriers(dependencyGraph, handlerGroup));
				final List<EventHandler<MessagingEvent>> eventHandlers = this.getEventHandlers(handlerGroup);
				final List<BatchEventProcessor<MessagingEvent>> batchEventProcessors = new ArrayList<BatchEventProcessor<MessagingEvent>>();
				for (final EventHandler<MessagingEvent> eventHandler : eventHandlers) {
					final BatchEventProcessor<MessagingEvent> batchEventProcessor = new BatchEventProcessor<MessagingEvent>(ringBuffer,
							ringBuffer.newBarrier(barriers), eventHandler);
					executor.submit(batchEventProcessor);
					batchEventProcessors.add(batchEventProcessor);
				}
				dependencyGraph.putData(handlerGroupName, batchEventProcessors);

			}
		}

		final List<String> gatingDependencies = inverseDependencyGraph.getOrphanDependencies();

		final Sequence[] gatingSequences = toArray(this.findGatingSequences(dependencyGraph, gatingDependencies));

		ringBuffer.setGatingSequences(gatingSequences);

		if (this.disruptorWorkflow == null) {
			this.disruptorWorkflow = new DisruptorWorkflow(ringBuffer);
		}

		this.registerEventDrivenConstumers();

	}

	private static Sequence[] toArray(final List<Sequence> sequences) {
		return sequences.toArray(new Sequence[sequences.size()]);
	}

	private List<Sequence> findGatingSequences(final DependencyGraph<List<? extends EventProcessor>> dependencyGraph, final List<String> gatingDependencies) {
		final List<Sequence> sequences = new ArrayList<Sequence>();
		for (final String gatingDependency : gatingDependencies) {
			sequences.addAll(this.findBarriers(dependencyGraph, this.handlerGroups.get(gatingDependency)));
		}
		return sequences;
	}

	private List<Sequence> findBarriers(final DependencyGraph<List<? extends EventProcessor>> dependencyGraph, final HandlerGroup handlerGroup) {
		final List<String> dependencies = dependencyGraph.getDependencies(handlerGroup.getName());
		final List<EventProcessor> allDependeeEventProcessors = new ArrayList<EventProcessor>();
		for (final String dependency : dependencies) {
			final List<? extends EventProcessor> dependeeEventProcessors = dependencyGraph.getData(dependency);
			allDependeeEventProcessors.addAll(dependeeEventProcessors);

		}
		final List<Sequence> sequences = new ArrayList<Sequence>(allDependeeEventProcessors.size());
		for (final EventProcessor dependeeEventProcessors : allDependeeEventProcessors) {
			sequences.add(dependeeEventProcessors.getSequence());
		}
		return sequences;
	}

	public List<EventHandler<MessagingEvent>> getEventHandlers(final HandlerGroup handlerGroup) {
		final List<EventHandler<MessagingEvent>> eventHandlers = new ArrayList<EventHandler<MessagingEvent>>();
		for (final String handlerBeanName : handlerGroup.getHandlerBeanNames()) {
			eventHandlers.add(new EventHandler<MessagingEvent>() {

				public void onEvent(final MessagingEvent event, final long sequence, final boolean endOfBatch) throws Exception {
					System.out.println(handlerGroup.getName() + " " + handlerBeanName + "> " + event);
				}

			});
		}
		return eventHandlers;
	}

	private RingBuffer<MessagingEvent> createRingBuffer() {
		return new RingBuffer<MessagingEvent>(new EventFactory<MessagingEvent>() {

			public MessagingEvent newInstance() {
				return new MessagingEvent();
			}

		}, 1024);
	}

	private void registerEventDrivenConstumers() {
		for (final String publisherChannelName : this.publisherChannelNames) {
			final SubscribableChannel subscribableChannel = this.beanFactory.getBean(publisherChannelName, SubscribableChannel.class);
			this.consumers.add(new EventDrivenConsumer(subscribableChannel, this.disruptorWorkflow));
		}
	}

	public Class<?> getObjectType() {
		return DisruptorWorkflow.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void start() {
		for (final EventDrivenConsumer consumer : this.consumers) {
			consumer.start();
		}
		this.disruptorWorkflow.start();
	}

	public void stop() {
		this.disruptorWorkflow.stop();
		for (final EventDrivenConsumer consumer : this.consumers) {
			consumer.stop();
		}
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
