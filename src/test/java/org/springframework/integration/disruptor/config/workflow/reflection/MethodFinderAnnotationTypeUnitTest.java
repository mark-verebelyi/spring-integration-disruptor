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

public class MethodFinderAnnotationTypeUnitTest {

	private final SingleAnnotatedMethod singleAnnotatedMethod = new SingleAnnotatedMethod();
	private final MultipleAnnotatedMethods multipleAnnotatedMethods = new MultipleAnnotatedMethods();

	@Test
	public void Single_annotation_type() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setAnnotationType(SomeAnnotation.class);

		final List<Method> methods = MethodFinderUtils.findMethods(this.singleAnnotatedMethod, specification);
		assertEquals(1, methods.size());

		assertEquals(ReflectionUtils.findMethod(SingleAnnotatedMethod.class, "doWhatEver"), methods.get(0));

	}

	@Test
	public void Multiple_annotated_methods() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setAnnotationType(SomeAnnotation.class);

		final List<Method> methods = MethodFinderUtils.findMethods(this.multipleAnnotatedMethods, specification);
		assertEquals(2, methods.size());

		final Method doWhateverMethod = ReflectionUtils.findMethod(MultipleAnnotatedMethods.class, "doWhatEver");
		final Method newObjectMethod = ReflectionUtils.findMethod(MultipleAnnotatedMethods.class, "newObject");
		assertTrue(methods.containsAll(Arrays.asList(doWhateverMethod, newObjectMethod)));
	}

	public static class SingleAnnotatedMethod {

		@SomeAnnotation
		public void doWhatEver() {
		}

		public void doAnother() {
		}

	}

	public static class MultipleAnnotatedMethods extends SingleAnnotatedMethod {

		public int newInt() {
			return 0;
		}

		@SomeAnnotation
		public Object newObject() {
			return new Object();
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface SomeAnnotation {

	}

}
