package org.springframework.integration.disruptor.config.workflow.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

public class MethodFinderImpl implements MethodFinder {

	public List<Method> findMethods(final Object target, final MethodSpecification specification) {

		final List<MethodFilter> methodFilters = new ArrayList<MethodFilter>();
		if (specification.hasReturnType()) {
			methodFilters.add(new MethodReturnTypeFilter(specification.getReturnType()));
		}
		if (specification.hasAnnotationType()) {
			methodFilters.add(new AnnotationTypeFilter(specification.getAnnotationType()));
		}

		methodFilters.add(ReflectionUtils.USER_DECLARED_METHODS);

		final MethodFilter compositeFilter = new CompositeFilter(methodFilters);

		final List<Method> methods = new ArrayList<Method>();

		ReflectionUtils.doWithMethods(target.getClass(), new MethodCallback() {

			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				methods.add(method);
			};

		}, compositeFilter);

		return methods;
	}

	private static class CompositeFilter implements MethodFilter {

		private List<MethodFilter> filters = new ArrayList<MethodFilter>();

		public CompositeFilter(final List<MethodFilter> filters) {
			this.filters = filters;
		}

		public boolean matches(final Method method) {
			for (final MethodFilter filter : this.filters) {
				if (!filter.matches(method)) {
					return false;
				}
			}
			return true;
		}

	}

	private static class AnnotationTypeFilter implements MethodFilter {

		private final Class<? extends Annotation> annotationType;

		public AnnotationTypeFilter(final Class<? extends Annotation> annotationType) {
			this.annotationType = annotationType;
		}

		public boolean matches(final Method method) {
			return method.isAnnotationPresent(this.annotationType);
		}

	}

	private static class MethodReturnTypeFilter implements MethodFilter {

		private final Class<?> returnType;

		public MethodReturnTypeFilter(final Class<?> returnType) {
			this.returnType = voidToPrimitive(returnType);
		}

		private static Class<?> voidToPrimitive(final Class<?> returnType) {
			return Void.class.equals(returnType) ? void.class : returnType;
		}

		public boolean matches(final Method method) {
			return ClassUtils.isAssignable(this.returnType, method.getReturnType());
		}

	}

}
