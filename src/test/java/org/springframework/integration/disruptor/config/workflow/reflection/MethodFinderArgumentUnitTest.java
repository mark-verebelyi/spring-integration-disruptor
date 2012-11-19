package org.springframework.integration.disruptor.config.workflow.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public class MethodFinderArgumentUnitTest {

	private final NoArg noArg = new NoArg();
	private final SomeArgs someArgs = new SomeArgs();
	private final AssignableArgs assignableArgs = new AssignableArgs();

	@Test
	public void NoArg() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setArgumentTypes();

		final List<Method> methods = MethodFinderUtils.findMethods(this.noArg, specification);
		assertEquals(2, methods.size());

		final Method noArg1Method = ReflectionUtils.findMethod(NoArg.class, "noArg1");
		final Method noArg2Method = ReflectionUtils.findMethod(NoArg.class, "noArg2");
		assertTrue(methods.containsAll(Arrays.asList(noArg1Method, noArg2Method)));

	}

	@Test
	public void SomeArgs() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setArgumentTypes(String.class, Integer.class);

		final List<Method> methods = MethodFinderUtils.findMethods(this.someArgs, specification);
		assertEquals(2, methods.size());

		final Method twoArgs1Method = ReflectionUtils.findMethod(SomeArgs.class, "twoArgs1", String.class, Integer.class);
		final Method twoArgs2Method = ReflectionUtils.findMethod(SomeArgs.class, "twoArgs2", String.class, Integer.class);
		assertTrue(methods.containsAll(Arrays.asList(twoArgs1Method, twoArgs2Method)));

	}

	@Test
	public void AssignableArgs() {

		final MethodSpecification specification = new MethodSpecification();
		specification.setArgumentTypes(Integer.class, Integer.class, Object.class);

		final List<Method> methods = MethodFinderUtils.findMethods(this.assignableArgs, specification);
		assertEquals(2, methods.size());

		final Method threeArgs1Method = ReflectionUtils.findMethod(AssignableArgs.class, "threeArgs1", Number.class, Integer.class, Object.class);
		final Method threeArgs2Method = ReflectionUtils.findMethod(AssignableArgs.class, "threeArgs2", Integer.class, int.class, Object.class);
		assertTrue(methods.containsAll(Arrays.asList(threeArgs1Method, threeArgs2Method)));

	}

	public static class SomeArgs {

		public int twoArgs1(final String arg1, final Integer arg2) {
			return 0;
		}

		public void threeArgs1(final Number arg1, final Integer arg2, final Object arg3) {
		}

		public void twoArgs2(final String arg1, final Integer arg2) {
		}

	}

	public static class AssignableArgs extends SomeArgs {

		public int threeArgs2(final Integer arg1, final int arg2, final Object arg3) {
			return 8;
		}

	}

	public static class NoArg {

		public void noArg1() {
		}

		public int noArg2() {
			return 9;
		}

		public void oneArg(final int arg) {
		}

	}

}
