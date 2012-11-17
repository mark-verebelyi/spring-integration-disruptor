package org.springframework.integration.disruptor.config.workflow;

import java.lang.reflect.Method;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.lmax.disruptor.EventFactory;

final class EventFactoryValidator {

	boolean canProduce(final Object object, final Class<?> eventType) {
		Assert.isTrue(eventType != null, "Event type can not be null");
		if (object instanceof EventFactory) {
			final Method method = ReflectionUtils.findMethod(object.getClass(), "newInstance");
			final Class<?> returnType = method.getReturnType();
			return eventType.isAssignableFrom(returnType);
		}
		return false;
	}

}
