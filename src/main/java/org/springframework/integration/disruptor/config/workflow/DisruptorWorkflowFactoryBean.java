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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.Message;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.disruptor.DisruptorWorkflow;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.integration.disruptor.config.HandlerGroup;
import org.springframework.integration.disruptor.config.workflow.eventfactory.FallbackEventFactoryAdapter;
import org.springframework.integration.disruptor.config.workflow.eventfactory.MethodInvokingEventFactoryAdapter;
import org.springframework.integration.disruptor.config.workflow.eventhandler.MethodInvokingEventHandler;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;
import org.springframework.integration.disruptor.config.workflow.translator.MessagingEventTranslator;
import org.springframework.integration.disruptor.config.workflow.translator.MethodInvokingMessageEventTranslator;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;

public final class DisruptorWorkflowFactoryBean<T> implements FactoryBean<DisruptorWorkflow<T>>, BeanFactoryAware, SmartLifecycle, InitializingBean {

	private final Log log = LogFactory.getLog(this.getClass());

	private String id;

	public void setId(final String id) {
		this.id = id;
	}

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

	private DisruptorWorkflow<T> disruptorWorkflow;

	private final Map<String, HandlerGroup> handlerGroups;
	private final DependencyGraph<List<EventProcessor>> dependencyGraph;
	private final DependencyGraph<List<EventProcessor>> inverseDependencyGraph;
	private final List<EventDrivenConsumer> consumers;

	public DisruptorWorkflow<T> getObject() throws Exception {
		return this.disruptorWorkflow;
	}

	public DisruptorWorkflowFactoryBean(final Map<String, HandlerGroup> handlerGroups) {
		this.handlerGroups = handlerGroups;
		this.dependencyGraph = createDependencyGraph(this.handlerGroups.values());
		this.inverseDependencyGraph = this.dependencyGraph.inverse();
		this.consumers = new ArrayList<EventDrivenConsumer>();
	}

	public void afterPropertiesSet() throws Exception {
		if (this.disruptorWorkflow == null) {
			final RingBuffer<T> ringBuffer = this.createRingBuffer();
			final List<EventProcessor> eventProcessors = this.collectEventProcessors();
			final Executor executor = this.createExecutorService(eventProcessors.size());
			final MessageEventTranslator<T> translator = this.createTranslator();
			this.disruptorWorkflow = new DisruptorWorkflow<T>(ringBuffer, executor, eventProcessors, translator);
			this.registerEventDrivenConstumers();
		}
	}

	private RingBuffer<T> createRingBuffer() {
		final EventFactory<T> eventFactory = this.createEventFactory();
		final RingBuffer<T> ringBuffer = this.createRingBuffer(eventFactory);
		this.setHandlers(ringBuffer);
		this.setGatingSequences(ringBuffer);
		return ringBuffer;
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

	private EventFactory<T> createEventFactory() {
		if (StringUtils.hasText(this.eventFactoryName)) {
			final Object object = this.beanFactory.getBean(this.eventFactoryName);
			if (this.isNativeEventFactory(object)) {
				this.log.info("Configuring '" + this.id + "' with native EventFactory named '" + this.eventFactoryName + "'.");
				@SuppressWarnings("unchecked")
				final EventFactory<T> eventFactory = (EventFactory<T>) object;
				return eventFactory;
			} else {
				this.log.info("Configuring '" + this.id + "' with MethodInvokingEventFactory named '" + this.eventFactoryName + "'.");
				return new MethodInvokingEventFactoryAdapter<T>(object, this.eventType);
			}
		}
		this.log.info("Configuring '" + this.id + "' with FallbackEventFactory.");
		return new FallbackEventFactoryAdapter<T>(this.eventType);
	}

	private boolean isNativeEventFactory(final Object eventFactory) {
		final EventFactoryValidator validator = new EventFactoryValidator();
		return validator.canProduce(eventFactory, this.eventType);
	}

	private List<EventProcessor> collectEventProcessors() {
		final List<String> topologyOrder = buildTopologyOrder(this.dependencyGraph);
		final List<EventProcessor> eventProcessors = new ArrayList<EventProcessor>();
		for (final String groupName : topologyOrder) {
			if ("ring-buffer".equals(groupName)) {
				continue;
			}
			eventProcessors.addAll(this.dependencyGraph.getData(groupName));
		}
		return eventProcessors;
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

	private void setHandlers(final RingBuffer<T> ringBuffer) {
		final List<String> handlerGroupNames = buildTopologyOrder(this.inverseDependencyGraph);
		for (final String handlerGroupName : handlerGroupNames) {
			final HandlerGroup handlerGroup = this.handlerGroups.get(handlerGroupName);
			if ("ring-buffer".equals(handlerGroupName)) {
				continue;
			} else {
				final SequenceBarrier barrier = this.createSequenceBarrier(ringBuffer, this.dependencyGraph, handlerGroup);
				final List<EventProcessor> eventProcessors = this.createEventProcessors(ringBuffer, handlerGroup, barrier);
				this.dependencyGraph.putData(handlerGroupName, eventProcessors);
			}
		}
	}

	private void setGatingSequences(final RingBuffer<T> ringBuffer) {
		final List<String> gatingDependencies = this.inverseDependencyGraph.getOrphanDependencies();
		final Sequence[] gatingSequences = toArray(this.findGatingSequences(gatingDependencies));
		ringBuffer.setGatingSequences(gatingSequences);
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

	private static List<String> buildTopologyOrder(final DependencyGraph<?> inverseDependencyGraph) {
		final DependencyTopologyBuilder topologyBuilder = new DependencyTopologyBuilderImpl();
		return topologyBuilder.buildTopology(inverseDependencyGraph);
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

	private List<EventProcessor> createEventProcessors(final RingBuffer<T> ringBuffer, final HandlerGroup handlerGroup, final SequenceBarrier barrier) {
		final List<EventHandler<T>> eventHandlers = this.getEventHandlers(handlerGroup);
		final List<EventProcessor> eventProcessors = new ArrayList<EventProcessor>();
		for (final EventHandler<T> eventHandler : eventHandlers) {
			final EventProcessor eventProcessor = new BatchEventProcessor<T>(ringBuffer, barrier, eventHandler);
			eventProcessors.add(eventProcessor);
		}
		return eventProcessors;
	}

	private static Sequence[] toArray(final List<Sequence> sequences) {
		return sequences.toArray(new Sequence[sequences.size()]);
	}

	private List<Sequence> findGatingSequences(final List<String> gatingDependencies) {
		final List<Sequence> sequences = new ArrayList<Sequence>();
		for (final String gatingDependency : gatingDependencies) {
			final HandlerGroup handlerGroup = this.handlerGroups.get(gatingDependency);
			final List<Sequence> gatingSequences = this.findBarriers(handlerGroup);
			sequences.addAll(gatingSequences);
		}
		return sequences;
	}

	private List<Sequence> findBarriers(final HandlerGroup handlerGroup) {
		final List<EventProcessor> allDependeeEventProcessors = this.getDependeeEventProcessors(handlerGroup);
		final List<Sequence> sequences = new ArrayList<Sequence>(allDependeeEventProcessors.size());
		for (final EventProcessor dependeeEventProcessors : allDependeeEventProcessors) {
			sequences.add(dependeeEventProcessors.getSequence());
		}
		return sequences;
	}

	private List<EventProcessor> getDependeeEventProcessors(final HandlerGroup handlerGroup) {
		final List<String> dependencies = this.dependencyGraph.getDependencies(handlerGroup.getName());
		final List<EventProcessor> allDependeeEventProcessors = new ArrayList<EventProcessor>();
		for (final String dependency : dependencies) {
			final List<EventProcessor> dependeeEventProcessors = this.dependencyGraph.getData(dependency);
			allDependeeEventProcessors.addAll(dependeeEventProcessors);

		}
		return allDependeeEventProcessors;
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

	private RingBuffer<T> createRingBuffer(final EventFactory<T> eventFactory) {
		return new RingBuffer<T>(eventFactory, 1024);
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
		return (this.disruptorWorkflow != null) && this.disruptorWorkflow.isRunning();
	}

	public int getPhase() {
		return Integer.MIN_VALUE;
	}

	public boolean isAutoStartup() {
		return (this.disruptorWorkflow != null) && this.disruptorWorkflow.isAutoStartup();
	}

	public void stop(final Runnable callback) {
		this.stop();
	}

}
