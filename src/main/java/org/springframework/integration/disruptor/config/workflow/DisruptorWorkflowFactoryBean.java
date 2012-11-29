package org.springframework.integration.disruptor.config.workflow;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.disruptor.DisruptorWorkflow;

import com.lmax.disruptor.RingBuffer;

public final class DisruptorWorkflowFactoryBean<T> extends AbstractDisruptorWorkflowFactoryBean<T> implements FactoryBean<Object> {

	private Class<?> interfaceClass;

	public void setInterfaceClass(final Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	@Override
	protected DisruptorWorkflow<T> createInstance() {
		final RingBuffer<T> ringBuffer = this.createRingBuffer();
		final Executor executor = this.createExecutor();
		return new DisruptorWorkflow<T>(ringBuffer, executor, this.handlerGroupDefinition.getAllEventProcessors());
	}

	public Object getObject() throws Exception {
		return this.getInstance();
	}

	public Class<?> getObjectType() {
		return null;
	}

	public boolean isSingleton() {
		return true;
	}

}
