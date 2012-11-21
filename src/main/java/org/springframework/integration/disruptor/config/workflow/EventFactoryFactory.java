package org.springframework.integration.disruptor.config.workflow;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.integration.disruptor.config.workflow.eventfactory.FallbackEventFactoryAdapter;
import org.springframework.integration.disruptor.config.workflow.eventfactory.MethodInvokingEventFactoryAdapter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.lmax.disruptor.EventFactory;

final class EventFactoryFactory<T> implements BeanFactoryAware {

	private final Log log = LogFactory.getLog(this.getClass());

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private String name;

	public void setName(final String name) {
		this.name = name;
	}

	private Class<T> eventType;

	public void setEventType(final Class<T> eventType) {
		this.eventType = eventType;
	}

	public EventFactory<T> createEventFactory() {
		if (StringUtils.hasText(this.name)) {
			final Object object = this.beanFactory.getBean(this.name);
			if (this.isNativeEventFactory(object)) {
				this.log.info("Configuring 'workflow' with native EventFactory named '" + this.name + "'.");
				@SuppressWarnings("unchecked")
				final EventFactory<T> eventFactory = (EventFactory<T>) object;
				return eventFactory;
			} else {
				this.log.info("Configuring 'workflow' with MethodInvokingEventFactory named '" + this.name + "'.");
				return new MethodInvokingEventFactoryAdapter<T>(object, this.eventType);
			}
		}
		this.log.info("Configuring 'workflow' with FallbackEventFactory.");
		return new FallbackEventFactoryAdapter<T>(this.eventType);
	}

	boolean isNativeEventFactory(final Object eventFactory) {
		Assert.isTrue(this.eventType != null, "Event type can not be null");
		if (eventFactory instanceof EventFactory) {
			final Method method = ReflectionUtils.findMethod(eventFactory.getClass(), "newInstance");
			final Class<?> returnType = method.getReturnType();
			return this.eventType.isAssignableFrom(returnType);
		}
		return false;
	}

}