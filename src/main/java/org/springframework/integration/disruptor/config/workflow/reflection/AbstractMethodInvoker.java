package org.springframework.integration.disruptor.config.workflow.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public abstract class AbstractMethodInvoker<T> {

	protected final Log log = LogFactory.getLog(this.getClass());

	protected final Object target;
	protected final Class<T> expectedType;
	protected final Method method;

	public AbstractMethodInvoker(final Object target, final Class<T> expectedType) {
		Assert.isTrue(target != null, "Target can not be null");
		Assert.isTrue(expectedType != null, "Expected type can not be null");
		this.target = target;
		this.expectedType = expectedType;
		this.method = this.findMethod();
	}

	protected String toPlain(final List<Method> suitableMethods) {
		final List<String> methodNames = new ArrayList<String>();
		for (final Method method : suitableMethods) {
			methodNames.add(method.getName());
		}
		Collections.sort(methodNames);
		return methodNames.toString();
	}

	protected Method findMethod() {
		final List<Method> suitableMethods = this.findSuitableMethods(this.target, this.expectedType);
		if (suitableMethods.isEmpty()) {
			throw new IllegalArgumentException("No suitable " + this.getDescription() + " method was found on " + this.target.getClass().getSimpleName());
		}
		return this.choose(suitableMethods);
	}

	private Method choose(final List<Method> suitableMethods) {
		if (suitableMethods.size() > 1) {
			final List<Method> narrowedSuitableMethods = this.narrowSuitableMethods(suitableMethods);
			if (narrowedSuitableMethods.size() != 1) {
				throw new IllegalArgumentException("Can't decide between multiple suitable " + this.getDescription() + " methods: "
						+ this.toPlain(suitableMethods) + "; consider using @" + this.getAnnotationType().getSimpleName() + " to designate one.");
			} else {
				return narrowedSuitableMethods.get(0);
			}
		} else {
			return suitableMethods.get(0);
		}
	}

	private List<Method> findSuitableMethods(final Object target, final Class<T> expectedType) {
		return MethodFinderUtils.findMethods(target, this.getSpecification(expectedType));
	}

	private List<Method> narrowSuitableMethods(final List<Method> suitableMethods) {
		return MethodFinderUtils.findMethods(suitableMethods, this.getNarrowingSpecification());
	}

	protected abstract MethodSpecification getSpecification(final Class<T> expectedType);

	protected abstract MethodSpecification getNarrowingSpecification();

	protected abstract String getDescription();

	protected abstract Class<? extends Annotation> getAnnotationType();

	protected T cast(final Object object) {
		return this.expectedType.cast(object);
	}

}