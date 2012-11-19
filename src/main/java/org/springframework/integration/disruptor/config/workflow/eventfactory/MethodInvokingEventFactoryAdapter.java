package org.springframework.integration.disruptor.config.workflow.eventfactory;

import java.lang.annotation.Annotation;

import org.springframework.integration.disruptor.config.annotation.EventFactory;
import org.springframework.integration.disruptor.config.workflow.reflection.AbstractMethodInvoker;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodSpecification;
import org.springframework.util.ReflectionUtils;

public class MethodInvokingEventFactoryAdapter<T> extends AbstractMethodInvoker<T> implements com.lmax.disruptor.EventFactory<T> {

	public MethodInvokingEventFactoryAdapter(final Object target, final Class<T> expectedType) {
		super(target, expectedType);
	}

	@Override
	protected MethodSpecification getSpecification(final Class<T> expectedType) {
		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(expectedType);
		specification.setArgumentTypes();
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
		return "EventFactory";
	}

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return EventFactory.class;
	}

	public T newInstance() {
		return this.cast(ReflectionUtils.invokeMethod(this.method, this.target));
	}

}
