package org.springframework.integration.disruptor.config.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.integration.disruptor.config.HandlerGroup;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;

public final class HandlerGroupDefinition {

	private final Map<String, HandlerGroup> handlerGroups;
	private final Map<String, List<Sequence>> sequencesForGroups;

	public HandlerGroupDefinition(final Map<String, HandlerGroup> handlerGroups) {
		this.handlerGroups = handlerGroups;
		this.sequencesForGroups = new HashMap<String, List<Sequence>>(this.handlerGroups.size());
	}

	public HandlerGroup getHandlerGroup(final String handlerGroupName) {
		return this.handlerGroups.get(handlerGroupName);
	}

	public void addEventProcessors(final String forHandlerGroup, final List<EventProcessor> eventProcessors) {
		this.addEventProcessorsToHandlerGroup(forHandlerGroup, eventProcessors);
		this.addSequencesToHandlerGroup(forHandlerGroup, eventProcessors);
	}

	private void addSequencesToHandlerGroup(final String forHandler, final List<EventProcessor> eventProcessors) {
		this.handlerGroups.get(forHandler).setEventProcessors(eventProcessors);
	}

	private void addEventProcessorsToHandlerGroup(final String forHandler, final List<EventProcessor> eventProcessors) {
		this.sequencesForGroups.put(forHandler, getSequences(eventProcessors));
	}

	private static List<Sequence> getSequences(final List<EventProcessor> eventProcessors) {
		final List<Sequence> sequences = new ArrayList<Sequence>(eventProcessors.size());
		for (final EventProcessor eventProcessor : eventProcessors) {
			sequences.add(eventProcessor.getSequence());
		}
		return sequences;
	}

	public List<EventProcessor> getAllEventProcessors() {
		final List<EventProcessor> allEventProcessors = new ArrayList<EventProcessor>();
		for (final HandlerGroup handlerGroup : this.handlerGroups.values()) {
			allEventProcessors.addAll(handlerGroup.getEventProcessors());
		}
		return allEventProcessors;
	}

	public List<EventProcessor> getEventProcessors(final String forHandlerGroup) {
		return this.handlerGroups.get(forHandlerGroup).getEventProcessors();
	}

	public List<Sequence> getSequences(final String handlerGroupName) {
		return this.sequencesForGroups.get(handlerGroupName);
	}

	public List<Sequence> getAllSequences(final Iterable<String> handlerGroupNames) {
		final List<Sequence> allSequences = new ArrayList<Sequence>();
		for (final String handlerGroupName : handlerGroupNames) {
			allSequences.addAll(this.getSequences(handlerGroupName));
		}
		return allSequences;
	}

	public DependencyGraph createDependencyGraph() {
		final DependencyGraph dependencyGraph = DependencyGraphImpl.forHandlerGroups(this.handlerGroups.values());
		detectDependencyCycle(dependencyGraph);
		return dependencyGraph;
	}

	private static void detectDependencyCycle(final DependencyGraph dependencyGraph) {
		final CycleDetector cycleDetector = new CycleDetectorImpl();
		if (cycleDetector.hasCycle(dependencyGraph)) {
			throw new BeanCreationException("Circular 'handler-group' dependency detected while creating DisruptorWorkflow");
		}
	}

}
