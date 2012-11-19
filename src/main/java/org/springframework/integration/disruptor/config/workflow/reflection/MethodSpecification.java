package org.springframework.integration.disruptor.config.workflow.reflection;

import java.lang.annotation.Annotation;

public class MethodSpecification {

	private Class<?> returnType;
	private Class<? extends Annotation> annotationType;

	public Class<?> getReturnType() {
		return this.returnType;
	}

	public void setReturnType(final Class<?> returnType) {
		this.returnType = returnType;
	}

	public Class<? extends Annotation> getAnnotationType() {
		return this.annotationType;
	}

	public void setAnnotationType(final Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	public boolean hasReturnType() {
		return this.returnType != null;
	}

	public boolean hasAnnotationType() {
		return this.annotationType != null;
	}

}
