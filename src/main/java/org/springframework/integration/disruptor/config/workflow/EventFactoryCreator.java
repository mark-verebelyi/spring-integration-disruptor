package org.springframework.integration.disruptor.config.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.integration.disruptor.config.workflow.eventfactory.FallbackEventFactoryAdapter;
import org.springframework.integration.disruptor.config.workflow.eventfactory.MethodInvokingEventFactoryAdapter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.lmax.disruptor.EventFactory;

final class EventFactoryCreator<T> {

	private final Log log = LogFactory.getLog(this.getClass());

	private final BeanFactory beanFactory;

	EventFactoryCreator(final BeanFactory beanFactory) {
		Assert.isTrue(beanFactory != null, "BeanFactory can not be null");
		this.beanFactory = beanFactory;
	}

	public EventFactory<T> createEventFactory(final Class<T> eventType, final String name) {
		if (StringUtils.hasText(name)) {
			final Object object = this.beanFactory.getBean(name);
			if (this.isNativeEventFactory(object, eventType)) {
				this.log.info("Configuring 'workflow' with native EventFactory named '" + name + "'.");
				@SuppressWarnings("unchecked")
				final EventFactory<T> eventFactory = (EventFactory<T>) object;
				return eventFactory;
			} else {
				this.log.info("Configuring 'workflow' with MethodInvokingEventFactory named '" + name + "'.");
				return new MethodInvokingEventFactoryAdapter<T>(object, eventType);
			}
		}
		this.log.info("Configuring 'workflow' with FallbackEventFactory.");
		return new FallbackEventFactoryAdapter<T>(eventType);
	}

	private boolean isNativeEventFactory(final Object eventFactory, final Class<T> eventType) {
		final EventFactoryValidator validator = new EventFactoryValidator();
		return validator.canProduce(eventFactory, eventType);
	}

}