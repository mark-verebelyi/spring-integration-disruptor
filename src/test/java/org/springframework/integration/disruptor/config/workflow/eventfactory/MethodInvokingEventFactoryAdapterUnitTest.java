package org.springframework.integration.disruptor.config.workflow.eventfactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.integration.disruptor.config.annotation.EventFactory;

public class MethodInvokingEventFactoryAdapterUnitTest {

	private final SingleMethodWithExpectedType singleMethodWithExpectedType = new SingleMethodWithExpectedType();
	private final MultipleMethodsWithExpectedType multipleMethodsWithExpectedType = new MultipleMethodsWithExpectedType();
	private final NoCompatibleReturnTypes noCompatibleReturnTypes = new NoCompatibleReturnTypes();
	private final PolymorphicReturnType polymorphicReturnType = new PolymorphicReturnType();
	private final EventFactoryAnnotationPresent eventFactoryAnnotationPresent = new EventFactoryAnnotationPresent();

	@Test(expected = IllegalArgumentException.class)
	public void Target_can_not_be_null() {
		new MethodInvokingEventFactoryAdapter<String>(null, String.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ExpectedType_can_not_be_null() {
		new MethodInvokingEventFactoryAdapter<String>(this.singleMethodWithExpectedType, null);
	}

	@Test
	public void Single_method_with_expected_type() {
		final MethodInvokingEventFactoryAdapter<String> adapter = new MethodInvokingEventFactoryAdapter<String>(this.singleMethodWithExpectedType, String.class);
		assertEquals("some-value", adapter.newInstance());
	}

	@Test
	public void Multiple_methods_with_expected_types() {
		try {
			new MethodInvokingEventFactoryAdapter<String>(this.multipleMethodsWithExpectedType, String.class);
			fail("Should have failed, when multiple methods apply.");
		} catch (final IllegalArgumentException e) {
			assertEquals("Can't decide between multiple suitable EventFactory methods: [newValue1, newValue2]; consider using @EventFactory to designate one.",
					e.getMessage());
		}
	}

	@Test
	public void No_compatible_return_types() {
		try {
			new MethodInvokingEventFactoryAdapter<String>(this.noCompatibleReturnTypes, String.class);
			fail("Should have failed, when there are no compatible return types");
		} catch (final IllegalArgumentException e) {
			assertEquals("No suitable EventFactory method was found on NoCompatibleReturnTypes", e.getMessage());
		}
	}

	@Test
	public void Polymorphic_return_types() {
		final MethodInvokingEventFactoryAdapter<Number> adapter = new MethodInvokingEventFactoryAdapter<Number>(this.polymorphicReturnType, Number.class);
		assertEquals(9, adapter.newInstance());
	}

	@Test
	public void EventFactory_annotation_present() {
		final MethodInvokingEventFactoryAdapter<Number> adapter = new MethodInvokingEventFactoryAdapter<Number>(this.eventFactoryAnnotationPresent,
				Number.class);
		assertEquals(10, adapter.newInstance());
	}

	public static class EventFactoryAnnotationPresent {

		public Integer newInt1() {
			return 9;
		}

		@EventFactory
		public Integer newInt2() {
			return 10;
		}

		public Integer newInt3() {
			return 9;
		}

	}

	public static class PolymorphicReturnType {

		public Integer newInt() {
			return 9;
		}

		public String newString() {
			return "Foo";
		}

	}

	public static class NoCompatibleReturnTypes {

		public Integer newInt() {
			return 8;
		}

		public Thread newThread() {
			return new Thread();
		}

	}

	public static class MultipleMethodsWithExpectedType {

		public String newValue1() {
			return "value-1";
		}

		public String newValue2() {
			return "value-2";
		}

	}

	public static class SingleMethodWithExpectedType {

		public String newFactory() {
			return "some-value";
		}

	}

}
