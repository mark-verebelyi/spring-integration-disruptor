package org.springframework.integration.disruptor.config.workflow;

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.disruptor.DisruptorWorkflow;
import org.springframework.util.Assert;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.RingBuffer;

public final class DisruptorWorkflowFactoryBean<T> extends AbstractDisruptorWorkflowFactoryBean<T> implements FactoryBean<Object> {

	private volatile Class<?> interfaceClass;
	private volatile Object proxyInstance;

	public void setInterfaceClass(final Class<?> interfaceClass) {
		if (interfaceClass != null) {
			Assert.isTrue(interfaceClass.isInterface(), interfaceClass.getName() + " is not an interface.");
			this.interfaceClass = interfaceClass;
		}
	}

	@Override
	protected DisruptorWorkflow<T> createInstance(final RingBuffer<T> ringBuffer, final Executor executor, final List<EventProcessor> eventProcessors) {
		return new DisruptorWorkflow<T>(ringBuffer, executor, eventProcessors);
	}

	public Object getObject() throws Exception {
		if (this.interfaceClass == null) {
			return this.getInstance();
		} else {
			if (this.proxyInstance == null) {
				this.proxyInstance = null;
			}
			return this.proxyInstance;
		}
	}

	public Class<?> getObjectType() {
		return this.interfaceClass != null ? this.interfaceClass : DisruptorWorkflow.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
