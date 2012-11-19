package org.springframework.integration.disruptor.config.workflow.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public class MethodFinderReturnTypeUnitTest {

	private final SingleMethodWithExactReturnType singleMethodWithExactReturnType = new SingleMethodWithExactReturnType();
	private final MultipleMethodsWithExactReturnTypesAndInheritance multipleMethodsWithExactReturnTypesAndInheritance = new MultipleMethodsWithExactReturnTypesAndInheritance();
	private final CovariantReturnType covariantReturnType = new CovariantReturnType();
	private final VoidReturnType voidReturnType = new VoidReturnType();

	private final MethodFinder methodFinder = new MethodFinderImpl();

	@Test
	public void Single_method_with_exact_return_type() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(Integer.class);

		final List<Method> methods = this.methodFinder.findMethods(this.singleMethodWithExactReturnType, specification);
		assertEquals(1, methods.size());

		assertEquals(ReflectionUtils.findMethod(SingleMethodWithExactReturnType.class, "newInt"), methods.get(0));

	}

	@Test
	public void Multiple_methods_with_exact_return_types_and_inheritance() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(Integer.class);

		final List<Method> methods = this.methodFinder.findMethods(this.multipleMethodsWithExactReturnTypesAndInheritance, specification);
		assertEquals(2, methods.size());

		final Method newIntMethod = ReflectionUtils.findMethod(MultipleMethodsWithExactReturnTypesAndInheritance.class, "newInt");
		final Method newValueMethod = ReflectionUtils.findMethod(MultipleMethodsWithExactReturnTypesAndInheritance.class, "newValue");
		assertTrue(methods.containsAll(Arrays.asList(newIntMethod, newValueMethod)));

	}

	@Test
	public void Covariant_return_types_enabled() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(Number.class);

		final List<Method> methods = this.methodFinder.findMethods(this.covariantReturnType, specification);
		assertEquals(1, methods.size());

		assertEquals(ReflectionUtils.findMethod(CovariantReturnType.class, "newInt"), methods.get(0));

	}

	@Test
	public void No_match() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(Runnable.class);

		final List<Method> methods = this.methodFinder.findMethods(this.singleMethodWithExactReturnType, specification);
		assertTrue(methods.isEmpty());

	}

	@Test
	public void Void_return_type() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setReturnType(void.class);

		final List<Method> methods = this.methodFinder.findMethods(this.voidReturnType, specification);
		assertEquals(1, methods.size());

		assertEquals(ReflectionUtils.findMethod(VoidReturnType.class, "noop"), methods.get(0));

	}

	public static class SingleMethodWithExactReturnType {

		public Integer newInt() {
			return 6;
		}

	}

	public static class MultipleMethodsWithExactReturnTypesAndInheritance extends SingleMethodWithExactReturnType {

		public Integer newValue() {
			return 9;
		}

	}

	public static class CovariantReturnType {

		public Object newObject() {
			return new Thread();
		}

		public Integer newInt() {
			return 12;
		}

	}

	public static class VoidReturnType {

		public void noop() {
		}

	}

}
