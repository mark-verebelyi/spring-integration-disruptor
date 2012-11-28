package org.springframework.integration.disruptor.config.workflow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.disruptor.config.HandlerGroup;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.ClaimStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;

final class RingBufferFactory<T> implements BeanFactoryAware, InitializingBean {

	private DependencyGraph depGraph;
	private DependencyGraph inverseDepGraph;
	private EventHandlerFactory<T> eventHandlerFactory;

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private String eventFactoryName;

	public void setEventFactoryName(final String eventFactoryName) {
		this.eventFactoryName = eventFactoryName;
	}

	private HandlerGroupDefinition handlerGroupDefinition;

	public void setHandlerGroupDefinition(final HandlerGroupDefinition handlerGroupDefinition) {
		this.handlerGroupDefinition = handlerGroupDefinition;
	}

	private Class<T> eventType;

	public void setEventType(final Class<T> eventType) {
		this.eventType = eventType;
	}

	private WaitStrategy waitStrategy;

	public void setWaitStrategy(final WaitStrategy waitStrategy) {
		this.waitStrategy = waitStrategy;
	}

	private ClaimStrategy claimStrategy;

	public void setClaimStrategy(final ClaimStrategy claimStrategy) {
		this.claimStrategy = claimStrategy;
	}

	public void afterPropertiesSet() throws Exception {
		this.depGraph = this.handlerGroupDefinition.createDependencyGraph();
		this.inverseDepGraph = this.depGraph.inverse();
		this.eventHandlerFactory = this.createEventHandlerFactory();
	}

	public RingBuffer<T> createRingBuffer() {
		final RingBuffer<T> ringBuffer = this.initializeRingBuffer();
		this.setEventHandlers(ringBuffer);
		this.setGatingSequences(ringBuffer);
		return ringBuffer;
	}

	private RingBuffer<T> initializeRingBuffer() {
		final EventFactory<T> eventFactory = this.createEventFactory(this.eventType);
		return new RingBuffer<T>(eventFactory, this.claimStrategy, this.waitStrategy);
	}

	private void setEventHandlers(final RingBuffer<T> ringBuffer) {
		final List<String> handlerGroupNames = buildTopologyOrder(this.inverseDepGraph);
		for (final String handlerGroupName : handlerGroupNames) {
			final HandlerGroup handlerGroup = this.handlerGroupDefinition.getHandlerGroup(handlerGroupName);
			if ("ring-buffer".equals(handlerGroupName)) {
				continue;
			} else {
				final SequenceBarrier barrier = this.createSequenceBarrier(ringBuffer, handlerGroup);
				final List<EventProcessor> eventProcessors = this.createEventProcessors(ringBuffer, handlerGroup, barrier);
				this.handlerGroupDefinition.addEventProcessors(handlerGroupName, eventProcessors);
			}
		}
	}

	private SequenceBarrier createSequenceBarrier(final RingBuffer<T> ringBuffer, final HandlerGroup handlerGroup) {
		if (handlerGroup.hasSingleDependency("ring-buffer")) {
			return ringBuffer.newBarrier();
		} else {
			final Sequence[] barriers = toArray(this.findBarriers(handlerGroup));
			return ringBuffer.newBarrier(barriers);
		}
	}

	private List<Sequence> findBarriers(final HandlerGroup handlerGroup) {
		final List<EventProcessor> allDependeeEventProcessors = this.getDependeeEventProcessors(handlerGroup);
		final List<Sequence> sequences = new ArrayList<Sequence>(allDependeeEventProcessors.size());
		for (final EventProcessor dependeeEventProcessors : allDependeeEventProcessors) {
			sequences.add(dependeeEventProcessors.getSequence());
		}
		return sequences;
	}

	private void setGatingSequences(final RingBuffer<T> ringBuffer) {
		final List<String> gatingDependencies = this.inverseDepGraph.getOrphanDependencies();
		final Sequence[] gatingSequences = toArray(this.handlerGroupDefinition.getAllSequences(gatingDependencies));
		ringBuffer.setGatingSequences(gatingSequences);
	}

	private static List<String> buildTopologyOrder(final DependencyGraph inverseDependencyGraph) {
		final DependencyTopologyBuilder topologyBuilder = new DependencyTopologyBuilderImpl();
		return topologyBuilder.buildTopology(inverseDependencyGraph);
	}

	private EventFactory<T> createEventFactory(final Class<T> eventType) {
		final EventFactoryFactory<T> eventFactoryFactory = new EventFactoryFactory<T>();
		eventFactoryFactory.setBeanFactory(this.beanFactory);
		eventFactoryFactory.setName(this.eventFactoryName);
		eventFactoryFactory.setEventType(eventType);
		return eventFactoryFactory.createEventFactory();
	}

	private List<EventProcessor> getDependeeEventProcessors(final HandlerGroup handlerGroup) {
		final List<String> dependencies = this.depGraph.getDependencies(handlerGroup.getName());
		final List<EventProcessor> allDependeeEventProcessors = new ArrayList<EventProcessor>();
		for (final String dependency : dependencies) {
			final List<EventProcessor> dependeeEventProcessors = this.handlerGroupDefinition.getEventProcessors(dependency);
			allDependeeEventProcessors.addAll(dependeeEventProcessors);

		}
		return allDependeeEventProcessors;
	}

	private List<EventProcessor> createEventProcessors(final RingBuffer<T> ringBuffer, final HandlerGroup handlerGroup, final SequenceBarrier barrier) {
		final List<EventHandler<T>> eventHandlers = this.eventHandlerFactory.createEventHandlers(handlerGroup);
		final List<EventProcessor> eventProcessors = new ArrayList<EventProcessor>();
		for (final EventHandler<T> eventHandler : eventHandlers) {
			final EventProcessor eventProcessor = new BatchEventProcessor<T>(ringBuffer, barrier, eventHandler);
			eventProcessors.add(eventProcessor);
		}
		return eventProcessors;
	}

	private EventHandlerFactory<T> createEventHandlerFactory() {
		final EventHandlerFactory<T> eventHandlerFactory = new EventHandlerFactory<T>();
		eventHandlerFactory.setBeanFactory(this.beanFactory);
		eventHandlerFactory.setEventType(this.eventType);
		return eventHandlerFactory;
	}

	private static Sequence[] toArray(final List<Sequence> sequences) {
		return sequences.toArray(new Sequence[sequences.size()]);
	}

}