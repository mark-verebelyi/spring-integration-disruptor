package org.springframework.integration.disruptor.config.workflow;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.StringUtils;

final class ExecutorServiceFactory implements BeanFactoryAware {

	private final Log log = LogFactory.getLog(this.getClass());

	private BeanFactory beanFactory;

	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private String executorName;

	public void setExecutorName(final String executorName) {
		this.executorName = executorName;
	}

	public Executor createExecutorService() {
		if (StringUtils.hasText(this.executorName)) {
			this.log.info("Configuring DisruptorWorkflow with Executor named '" + this.executorName + "'.");
			return this.beanFactory.getBean(this.executorName, Executor.class);
		} else {
			this.log.info("No bean named 'executor' has been explicitly defined. Therefore, a default Executor will be created.");
			return Executors.newCachedThreadPool();
		}
	}

}