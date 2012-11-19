package org.springframework.integration.disruptor.config.workflow.eventhandler;

import java.lang.annotation.Annotation;

import org.springframework.integration.disruptor.config.workflow.reflection.AbstractMethodInvoker;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodSpecification;
import org.springframework.util.ReflectionUtils;

import com.lmax.disruptor.EventHandler;

public final class MethodInvokingEventHandler<T> extends AbstractMethodInvoker<T> implements EventHandler<T> {

	public MethodInvokingEventHandler(final Object target, final Class<T> expectedType) {
		super(target, expectedType);
	}

	public void onEvent(final T event, final long sequence, final boolean endOfBatch) throws Exception {
		ReflectionUtils.invokeMethod(this.method, this.target, event);
	}

	@Override
	protected MethodSpecification getSpecification(final Class<T> expectedType) {
		final MethodSpecification specification = new MethodSpecification();
		specification.setArgumentTypes(expectedType);
		specification.setReturnType(void.class);
		return specification;
	}

	@Override
	protected MethodSpecification getNarrowingSpecification() {
		final MethodSpecification specification = new MethodSpecification();
		specification.setAnnotationType(this.getAnnotationType());
		return specification;
	}

	@Override
	protected String getDescription() {
		return "EventHandler";
	}

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return org.springframework.integration.disruptor.config.annotation.EventHandler.class;
	}

}
