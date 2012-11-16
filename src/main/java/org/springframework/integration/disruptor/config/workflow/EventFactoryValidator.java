package org.springframework.integration.disruptor.config.workflow;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import com.lmax.disruptor.EventFactory;

public class EventFactoryValidator {

	private final Log log = LogFactory.getLog(this.getClass());

	public boolean canProduce(final EventFactory<?> eventFactory, final Class<?> eventType) {
		final Method method = ReflectionUtils.findMethod(eventFactory.getClass(), "newInstance");
		final Class<?> returnType = method.getReturnType();
		return returnType.equals(eventType);
	}

}
