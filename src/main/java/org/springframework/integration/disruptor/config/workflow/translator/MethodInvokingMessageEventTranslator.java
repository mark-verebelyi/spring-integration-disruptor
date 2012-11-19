package org.springframework.integration.disruptor.config.workflow.translator;

import java.lang.annotation.Annotation;

import org.springframework.integration.Message;
import org.springframework.integration.disruptor.config.workflow.reflection.AbstractMethodInvoker;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodSpecification;
import org.springframework.util.ReflectionUtils;

public class MethodInvokingMessageEventTranslator<T> extends AbstractMethodInvoker<T> implements MessageEventTranslator<T> {

	public MethodInvokingMessageEventTranslator(final Object target, final Class<T> expectedType) {
		super(target, expectedType);
	}

	@Override
	protected MethodSpecification getSpecification(final Class<T> expectedType) {
		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(void.class);
		specification.setArgumentTypes(Message.class, this.expectedType);
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
		return "MessageEventTranslator";
	}

	@Override
	protected Class<? extends Annotation> getAnnotationType() {
		return org.springframework.integration.disruptor.config.annotation.EventTranslator.class;
	}

	public void translateTo(final Message<?> message, final T event) {
		ReflectionUtils.invokeMethod(this.method, this.target, message, event);
	}

}
