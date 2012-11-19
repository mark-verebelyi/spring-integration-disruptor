package org.springframework.integration.disruptor.config.workflow.reflection;

import java.lang.reflect.Method;
import java.util.List;

public interface MethodFinder {

	List<Method> findMethods(Object target, MethodSpecification specification);

}
