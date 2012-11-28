package org.springframework.integration.disruptor.config.workflow;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.integration.disruptor.DisruptorWorkflow;

import com.lmax.disruptor.RingBuffer;

public final class DisruptorWorkflowFactoryBean<T> extends AbstractDisruptorWorkflowFactoryBean<T> implements FactoryBean<Object> {

	private Class<?> interfaceClass;

	public void setInterfaceClass(final Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	private DisruptorWorkflow<T> instance;

	@Override
	protected SmartLifecycle getInstance() {
		return this.instance;
	}

	@Override
	protected void createInstance() {
		final RingBuffer<T> ringBuffer = this.createRingBuffer();
		final Executor executor = this.createExecutor();
		this.instance = new DisruptorWorkflow<T>(ringBuffer, executor, this.handlerGroupDefinition.getAllEventProcessors());
	}

	public Object getObject() throws Exception {
		return this.instance;
	}

	public Class<?> getObjectType() {
		return null;
	}

	public boolean isSingleton() {
		return true;
	}

}
