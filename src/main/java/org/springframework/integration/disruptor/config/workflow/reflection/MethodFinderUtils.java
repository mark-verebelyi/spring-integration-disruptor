package org.springframework.integration.disruptor.config.workflow.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;

public final class MethodFinderUtils {

	private MethodFinderUtils() {
		throw new IllegalStateException("Utility class, do not instantiate.");
	}

	public static List<Method> findMethods(final Object target, final MethodSpecification specification) {
		final List<Method> methods = Arrays.asList(ReflectionUtils.getAllDeclaredMethods(target.getClass()));
		return findMethods(methods, specification);
	}

	public static List<Method> findMethods(final List<Method> methods, final MethodSpecification specification) {
		final MethodFilter filter = createCompositeMethodFilter(specification);
		return findMatchingMethods(methods, filter);
	}

	private static List<Method> findMatchingMethods(final List<Method> methods, final MethodFilter filter) {
		final List<Method> matchingMethods = new ArrayList<Method>();
		for (final Method method : methods) {
			if (filter.matches(method)) {
				matchingMethods.add(method);
			}
		}
		return matchingMethods;
	}

	private static CompositeFilter createCompositeMethodFilter(final MethodSpecification specification) {
		final List<MethodFilter> methodFilters = new ArrayList<MethodFilter>();
		addMethodReturnTypeFilter(methodFilters, specification);
		addAnnotationTypeFilter(methodFilters, specification);
		addArgumentTypesFilter(methodFilters, specification);
		addUserDeclaredMethodsFilter(methodFilters);
		return new CompositeFilter(methodFilters);
	}

	private static void addMethodReturnTypeFilter(final List<MethodFilter> methodFilters, final MethodSpecification specification) {
		if (specification.hasReturnType()) {
			methodFilters.add(new MethodReturnTypeFilter(specification.getReturnType()));
		}
	}

	private static void addAnnotationTypeFilter(final List<MethodFilter> methodFilters, final MethodSpecification specification) {
		if (specification.hasAnnotationType()) {
			methodFilters.add(new AnnotationTypeFilter(specification.getAnnotationType()));
		}
	}

	private static void addArgumentTypesFilter(final List<MethodFilter> methodFilters, final MethodSpecification specification) {
		if (specification.hasArgumentTypes()) {
			methodFilters.add(new ArgumentTypesFilter(specification.getArgumentTypes()));
		}
	}

	private static void addUserDeclaredMethodsFilter(final List<MethodFilter> methodFilters) {
		methodFilters.add(ReflectionUtils.USER_DECLARED_METHODS);
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

	private final static class ArgumentTypesFilter implements MethodFilter {

		private final Class<?>[] argumentTypes;

		private ArgumentTypesFilter(final Class<?>[] argumentTypes) {
			this.argumentTypes = argumentTypes;
		}

		public boolean matches(final Method method) {
			final Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != this.argumentTypes.length) {
				return false;
			}
			for (int i = 0; i < parameterTypes.length; i++) {
				if (!ClassUtils.isAssignable(parameterTypes[i], this.argumentTypes[i])) {
					return false;
				}
			}
			return true;
		}

	}

	private final static class AnnotationTypeFilter implements MethodFilter {

		private final Class<? extends Annotation> annotationType;

		private AnnotationTypeFilter(final Class<? extends Annotation> annotationType) {
			this.annotationType = annotationType;
		}

		public boolean matches(final Method method) {
			return method.isAnnotationPresent(this.annotationType);
		}

	}

	private final static class MethodReturnTypeFilter implements MethodFilter {

		private final Class<?> returnType;

		private MethodReturnTypeFilter(final Class<?> returnType) {
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
