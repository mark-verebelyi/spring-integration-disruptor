package org.springframework.integration.disruptor.config.workflow.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public class MethodFinderUnitTest {

	private final MethodFinder methodFinder = new MethodFinderImpl();

	@Test
	public void Composite_specification() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(Integer.class);
		specification.setAnnotationType(AnnotationA.class);
		specification.setArgumentTypes(String.class, int.class, Long.class);

		final List<Method> methods = this.methodFinder.findMethods(new C(), specification);
		assertEquals(2, methods.size());

		final Method createValueMethod = ReflectionUtils.findMethod(C.class, "createValue", String.class, Object.class, Number.class);
		final Method createSomeValueMethod = ReflectionUtils.findMethod(C.class, "createSomeValue", String.class, Object.class, Number.class);
		assertTrue(methods.containsAll(Arrays.asList(createValueMethod, createSomeValueMethod)));

	}

	public static class A {

		public int newInt(final String a, final Object b, final Number c) {
			return 9;
		}

		@AnnotationA
		public void noArg() {
		}

	}

	public static class B extends A {

		@AnnotationA
		public Integer createValue(final String a, final Object b, final Number c) {
			return 12;
		}

		@AnnotationB
		public Integer createAnotherValue(final String a, final Object b, final Number c) {
			return 121;
		}

	}

	public static class C extends B {

		@AnnotationA
		public int createSomeValue(final String a, final Object b, final Number c) {
			return 90;
		}

		@AnnotationA
		public String misc(final String a, final Object b, final Number c) {
			return "foo";
		}

		public Thread newThread(final String a, final Object b) {
			return new Thread();
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface AnnotationA {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface AnnotationB {

	}

}
