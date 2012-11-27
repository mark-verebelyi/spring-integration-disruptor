package org.springframework.integration.disruptor.config.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.disruptor.config.HandlerGroup;
import org.springframework.integration.disruptor.config.workflow.eventhandler.MethodInvokingEventHandler;
import org.springframework.util.ReflectionUtils;

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

	private final Log log = LogFactory.getLog(this.getClass());

	private DependencyGraph<List<EventProcessor>> depGraph;
	private DependencyGraph<List<EventProcessor>> inverseDepGraph;

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private String eventFactoryName;

	public void setEventFactoryName(final String eventFactoryName) {
		this.eventFactoryName = eventFactoryName;
	}

	private Map<String, HandlerGroup> handlerGroups;

	public void setHandlerGroups(final Map<String, HandlerGroup> handlerGroups) {
		this.handlerGroups = handlerGroups;
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
		this.depGraph = createDependencyGraph(this.handlerGroups.values());
		this.inverseDepGraph = this.depGraph.inverse();
	}

	public EventProcessorTrackingRingBuffer<T> createRingBuffer() {
		final EventProcessorTrackingRingBuffer<T> ringBuffer = this.initializeRingBuffer();
		this.setEventHandlers(ringBuffer);
		this.setGatingSequences(ringBuffer.getDelegate());
		return ringBuffer;
	}

	private EventProcessorTrackingRingBuffer<T> initializeRingBuffer() {
		final EventFactory<T> eventFactory = this.createEventFactory(this.eventType);
		return new EventProcessorTrackingRingBuffer<T>(new RingBuffer<T>(eventFactory, this.claimStrategy, this.waitStrategy));
	}

	private void setEventHandlers(final EventProcessorTrackingRingBuffer<T> ringBuffer) {
		final List<String> handlerGroupNames = buildTopologyOrder(this.inverseDepGraph);
		for (final String handlerGroupName : handlerGroupNames) {
			final HandlerGroup handlerGroup = this.handlerGroups.get(handlerGroupName);
			if ("ring-buffer".equals(handlerGroupName)) {
				continue;
			} else {
				final SequenceBarrier barrier = this.createSequenceBarrier(ringBuffer.getDelegate(), this.depGraph, handlerGroup);
				final List<EventProcessor> eventProcessors = this.createEventProcessors(ringBuffer.getDelegate(), handlerGroup, barrier);
				this.depGraph.putData(handlerGroupName, eventProcessors);
				ringBuffer.addEventProcessors(eventProcessors);
			}
		}
	}

	private SequenceBarrier createSequenceBarrier(final RingBuffer<T> ringBuffer, final DependencyGraph<List<EventProcessor>> dependencyGraph,
			final HandlerGroup handlerGroup) {
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
		final Sequence[] gatingSequences = toArray(this.findGatingSequences(gatingDependencies));
		ringBuffer.setGatingSequences(gatingSequences);
	}

	private static List<String> buildTopologyOrder(final DependencyGraph<?> inverseDependencyGraph) {
		final DependencyTopologyBuilder topologyBuilder = new DependencyTopologyBuilderImpl();
		return topologyBuilder.buildTopology(inverseDependencyGraph);
	}

	private static DependencyGraph<List<EventProcessor>> createDependencyGraph(final Iterable<HandlerGroup> handlerGroups) {
		final DependencyGraph<List<EventProcessor>> dependencyGraph = DependencyGraphImpl.forHandlerGroups(handlerGroups);
		detectDependencyCycle(dependencyGraph);
		return dependencyGraph;
	}

	private static void detectDependencyCycle(final DependencyGraph<List<EventProcessor>> dependencyGraph) {
		final CycleDetector cycleDetector = new CycleDetectorImpl();
		if (cycleDetector.hasCycle(dependencyGraph)) {
			throw new BeanCreationException("Circular 'handler-group' dependency detected while creating DisruptorWorkflow");
		}
	}

	private EventFactory<T> createEventFactory(final Class<T> eventType) {
		final EventFactoryFactory<T> eventFactoryFactory = new EventFactoryFactory<T>();
		eventFactoryFactory.setBeanFactory(this.beanFactory);
		eventFactoryFactory.setName(this.eventFactoryName);
		eventFactoryFactory.setEventType(eventType);
		return eventFactoryFactory.createEventFactory();
	}

	private List<Sequence> findGatingSequences(final List<String> gatingDependencies) {
		final List<Sequence> sequences = new ArrayList<Sequence>();
		for (final String gatingDependency : gatingDependencies) {
			final HandlerGroup handlerGroup = this.handlerGroups.get(gatingDependency);
			final List<Sequence> gatingSequences = this.findGatingBarriers(handlerGroup);
			sequences.addAll(gatingSequences);
		}
		return sequences;
	}

	private List<Sequence> findGatingBarriers(final HandlerGroup handlerGroup) {
		final List<EventProcessor> eventProcessors = this.depGraph.getData(handlerGroup.getName());
		final List<Sequence> gatingSequences = new ArrayList<Sequence>();
		for (final EventProcessor eventProcessor : eventProcessors) {
			gatingSequences.add(eventProcessor.getSequence());
		}
		return gatingSequences;
	}

	private List<EventProcessor> getDependeeEventProcessors(final HandlerGroup handlerGroup) {
		final List<String> dependencies = this.depGraph.getDependencies(handlerGroup.getName());
		final List<EventProcessor> allDependeeEventProcessors = new ArrayList<EventProcessor>();
		for (final String dependency : dependencies) {
			final List<EventProcessor> dependeeEventProcessors = this.depGraph.getData(dependency);
			allDependeeEventProcessors.addAll(dependeeEventProcessors);

		}
		return allDependeeEventProcessors;
	}

	private List<EventProcessor> createEventProcessors(final RingBuffer<T> ringBuffer, final HandlerGroup handlerGroup, final SequenceBarrier barrier) {
		final List<EventHandler<T>> eventHandlers = this.getEventHandlers(handlerGroup);
		final List<EventProcessor> eventProcessors = new ArrayList<EventProcessor>();
		for (final EventHandler<T> eventHandler : eventHandlers) {
			final EventProcessor eventProcessor = new BatchEventProcessor<T>(ringBuffer, barrier, eventHandler);
			eventProcessors.add(eventProcessor);
		}
		return eventProcessors;
	}

	public List<EventHandler<T>> getEventHandlers(final HandlerGroup handlerGroup) {
		final List<EventHandler<T>> eventHandlers = new ArrayList<EventHandler<T>>();
		for (final String handlerBeanName : handlerGroup.getHandlerBeanNames()) {
			eventHandlers.add(this.createEventHandler(handlerGroup, handlerBeanName));
		}
		return eventHandlers;
	}

	private EventHandler<T> createEventHandler(final HandlerGroup handlerGroup, final String handlerBeanName) {
		final Object handler = this.beanFactory.getBean(handlerBeanName);
		if (this.isNativeHandler(handler)) {
			this.log.info("'" + handlerBeanName + "' is a native EventHandler");
			@SuppressWarnings("unchecked")
			final EventHandler<T> eventHandler = (EventHandler<T>) handler;
			return eventHandler;
		} else {
			this.log.info("'" + handlerBeanName + "' is a not native EventHandler, wrapping with MethodInvokingEventHandler.");
			return new MethodInvokingEventHandler<T>(handler, this.eventType);
		}
	}

	private boolean isNativeHandler(final Object handler) {
		return (handler instanceof EventHandler)
				&& (ReflectionUtils.findMethod(handler.getClass(), "onEvent", this.eventType, long.class, boolean.class) != null);
	}

	private static Sequence[] toArray(final List<Sequence> sequences) {
		return sequences.toArray(new Sequence[sequences.size()]);
	}

}