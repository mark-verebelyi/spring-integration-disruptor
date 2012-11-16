package org.springframework.integration.disruptor.config.workflow.eventfactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import com.lmax.disruptor.EventFactory;

public class MethodInvokingEventFactoryAdapter<T> implements EventFactory<T> {

	private final Object target;
	private final Method method;
	private final Class<T> expectedType;

	public MethodInvokingEventFactoryAdapter(final Object target, final Class<T> expectedType) {

		this.target = target;

		final DefaultMethodCallback<T> callback = new DefaultMethodCallback<T>(expectedType);

		ReflectionUtils.doWithMethods(target.getClass(), callback, new DefaultMethodFilter());

		this.method = callback.getMethod();
		this.expectedType = expectedType;

	}

	public T newInstance() {
		return this.expectedType.cast(ReflectionUtils.invokeMethod(this.method, this.target));
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

		public Method getMethod() {
			return this.methods.get(0);
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

}
