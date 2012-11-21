package org.springframework.integration.disruptor.config.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.integration.Message;
import org.springframework.integration.disruptor.MessagingEvent;
import org.springframework.integration.disruptor.config.workflow.translator.MessageEventTranslator;
import org.springframework.integration.disruptor.config.workflow.translator.MessagingEventTranslator;
import org.springframework.integration.disruptor.config.workflow.translator.MethodInvokingMessageEventTranslator;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

final class MessageEventTranslatorFactory<T> implements BeanFactoryAware {

	private final Log log = LogFactory.getLog(this.getClass());

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private Class<T> eventType;

	public void setEventType(final Class<T> eventType) {
		this.eventType = eventType;
	}

	private String translatorName;

	public void setTranslatorName(final String translatorName) {
		this.translatorName = translatorName;
	}

	public MessageEventTranslator<T> createTranslator() {
		if (StringUtils.hasText(this.translatorName)) {
			final Object translator = this.beanFactory.getBean(this.translatorName);
			if (this.isNativeTranslator(translator)) {
				this.log.info("'" + this.translatorName + "' is a native MessageEventTranslator.");
				@SuppressWarnings("unchecked")
				final MessageEventTranslator<T> messageToEventTranslator = (MessageEventTranslator<T>) translator;
				return messageToEventTranslator;
			} else {
				this.log.info("'" + this.translatorName + "' is not a native MessageEventTranslator, configuring MethodInvokingMessageEventTranslator.");
				return new MethodInvokingMessageEventTranslator<T>(translator, this.eventType);
			}
		} else {
			if (this.isMessagingEventType()) {
				this.log.info("'MessagingEvent' event type found, configuring default MessageEventTranslator");
				@SuppressWarnings("unchecked")
				final MessageEventTranslator<T> messagingEventTranslator = (MessageEventTranslator<T>) new MessagingEventTranslator();
				return messagingEventTranslator;
			} else {
				throw new BeanCreationException("Can't create 'workflow' without MessageEventTranslator (the one exception "
						+ "to this rule is when event type is MessagingEvent or empty)");
			}
		}
	}

	private boolean isNativeTranslator(final Object translator) {
		return (translator instanceof MessageEventTranslator)
				&& (ReflectionUtils.findMethod(translator.getClass(), "translateTo", Message.class, this.eventType) != null);
	}

	private boolean isMessagingEventType() {
		return MessagingEvent.class.isAssignableFrom(this.eventType);
	}

}