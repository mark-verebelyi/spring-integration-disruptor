package org.springframework.integration.disruptor.config.workflow.eventfactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.disruptor.config.annotation.EventFactory;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodFinderUtils;
import org.springframework.integration.disruptor.config.workflow.reflection.MethodSpecification;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class MethodInvokingEventFactoryAdapter<T> implements com.lmax.disruptor.EventFactory<T> {

	private final Log log = LogFactory.getLog(this.getClass());

	private final Object target;
	private final Method method;
	private final Class<T> expectedType;

	public MethodInvokingEventFactoryAdapter(final Object target, final Class<T> expectedType) {
		Assert.isTrue(target != null, "Target can not be null");
		Assert.isTrue(expectedType != null, "Expected type can not be null");
		this.target = target;
		this.method = this.findMethod(target, expectedType);
		this.log.info("Using '" + this.method + "' on '" + target.getClass().getSimpleName() + "' for creating events of type '" + expectedType.getSimpleName()
				+ "'");
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
		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(expectedType);
		specification.setArgumentTypes();
		return MethodFinderUtils.findMethods(target, specification);
	}

	private List<Method> narrowSuitableMethods(final List<Method> suitableMethods) {
		final MethodSpecification specification = new MethodSpecification();
		specification.setAnnotationType(EventFactory.class);
		return MethodFinderUtils.findMethods(suitableMethods, specification);
	}

	private String toPlain(final List<Method> suitableMethods) {
		final List<String> methodNames = new ArrayList<String>();
		for (final Method method : suitableMethods) {
			methodNames.add(method.getName());
		}
		Collections.sort(methodNames);
		return methodNames.toString();
	}

}
