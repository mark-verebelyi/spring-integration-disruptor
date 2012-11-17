package org.springframework.integration.disruptor.config.workflow.eventfactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.integration.disruptor.config.annotation.EventFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

public class MethodInvokingEventFactoryAdapter<T> implements com.lmax.disruptor.EventFactory<T> {

	private final Object target;
	private final Method method;
	private final Class<T> expectedType;

	public MethodInvokingEventFactoryAdapter(final Object target, final Class<T> expectedType) {
		Assert.isTrue(target != null, "Target can not be null");
		Assert.isTrue(expectedType != null, "Expected type can not be null");
		this.target = target;
		this.method = this.findMethod(target, expectedType);
		this.expectedType = expectedType;
	}

	public T newInstance() {
		return this.expectedType.cast(ReflectionUtils.invokeMethod(this.method, this.target));
	}

	private Method findMethod(final Object target, final Class<T> expectedType) {
		final List<Method> suitableMethods = this.findSuitableMethods(target, expectedType);
		if (suitableMethods.isEmpty()) {
			throw new IllegalArgumentException("No suitable event factory method was found on " + target.getClass().getSimpleName());
		}
		return this.choose(suitableMethods);
	}

	private Method choose(final List<Method> suitableMethods) {
		if (suitableMethods.size() > 1) {
			final List<Method> narrowedSuitableMethods = this.narrowSuitableMethods(suitableMethods);
			if (narrowedSuitableMethods.size() != 1) {
				throw new IllegalArgumentException("Can't decide between multiple suitable event factory methods: " + this.toPlain(suitableMethods));
			} else {
				return narrowedSuitableMethods.get(0);
			}
		} else {
			return suitableMethods.get(0);
		}
	}

	private List<Method> findSuitableMethods(final Object target, final Class<T> expectedType) {
		final DefaultMethodCallback<T> callback = new DefaultMethodCallback<T>(expectedType);
		ReflectionUtils.doWithMethods(target.getClass(), callback, new DefaultMethodFilter());
		return callback.getMethods();
	}

	private List<Method> narrowSuitableMethods(final List<Method> suitableMethods) {
		final NarrowingMethodFilter narrowingMethodFilter = new NarrowingMethodFilter();
		final List<Method> narrowedSuitableMethods = new ArrayList<Method>();
		for (final Method suitableMethod : suitableMethods) {
			if (narrowingMethodFilter.matches(suitableMethod)) {
				narrowedSuitableMethods.add(suitableMethod);
			}
		}
		return narrowedSuitableMethods;
	}

	private String toPlain(final List<Method> suitableMethods) {
		final List<String> methodNames = new ArrayList<String>();
		for (final Method method : suitableMethods) {
			methodNames.add(method.getName());
		}
		Collections.sort(methodNames);
		return methodNames.toString();
	}

	private static final class DefaultMethodCallback<T> implements MethodCallback {

		private final List<Method> methods = new ArrayList<Method>();
		private final Class<T> expectedType;

		public DefaultMethodCallback(final Class<T> expectedType) {
			this.expectedType = expectedType;
		}

		public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
			if (this.hasExpectedReturnType(method) && this.hasNoArguments(method)) {
				this.methods.add(method);
			}
		}

		private boolean hasNoArguments(final Method method) {
			return method.getParameterTypes().length == 0;
		}

		private boolean hasExpectedReturnType(final Method method) {
			return this.expectedType.isAssignableFrom(method.getReturnType());
		}

		public List<Method> getMethods() {
			return this.methods;
		}

	}

	private static final class DefaultMethodFilter implements MethodFilter {

		public boolean matches(final Method method) {
			if (Object.class.equals(method.getDeclaringClass())) {
				return false;
			}
			return true;
		}

	}

	private static final class NarrowingMethodFilter implements MethodFilter {

		public boolean matches(final Method method) {
			if (method.isAnnotationPresent(EventFactory.class)) {
				return true;
			}
			return false;
		}

	}

}
