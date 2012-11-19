package org.springframework.integration.disruptor.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.integration.disruptor.DisruptorWorkflowIntegrationTest;
import org.springframework.integration.disruptor.SimpleChannelUseCaseIntegrationTest;
import org.springframework.integration.disruptor.config.DisruptorNamespaceIntegrationTest;
import org.springframework.integration.disruptor.config.RingBufferIntegrationTest;
import org.springframework.integration.disruptor.config.workflow.CycleDetectorUnitTest;
import org.springframework.integration.disruptor.config.workflow.DependencyGraphUnitTest;
import org.springframework.integration.disruptor.config.workflow.DependencyTopologyBuilderUnitTest;
import org.springframework.integration.disruptor.config.workflow.EventFactoryValidatorUnitTest;
import org.springframework.integration.disruptor.config.workflow.eventfactory.MethodInvokingEventFactoryAdapterUnitTest;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodFinderAnnotationTypeUnitTest;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodFinderArgumentUnitTest;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodFinderReturnTypeUnitTest;
import org.springframework.integration.disruptor.config.workflow.translator.MethodInvokingMessageEventTranslatorUnitTest;

@RunWith(Suite.class)
@SuiteClasses({ DisruptorWorkflowIntegrationTest.class, SimpleChannelUseCaseIntegrationTest.class, DisruptorNamespaceIntegrationTest.class,
		RingBufferIntegrationTest.class, CycleDetectorUnitTest.class, DependencyGraphUnitTest.class, DependencyTopologyBuilderUnitTest.class,
		EventFactoryValidatorUnitTest.class, MethodInvokingEventFactoryAdapterUnitTest.class, MethodFinderArgumentUnitTest.class,
		MethodFinderReturnTypeUnitTest.class, MethodFinderAnnotationTypeUnitTest.class, MethodInvokingMessageEventTranslatorUnitTest.class })
public class AllTests {

}
