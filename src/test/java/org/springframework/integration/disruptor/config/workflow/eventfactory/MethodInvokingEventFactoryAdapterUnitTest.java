package org.springframework.integration.disruptor.config.workflow.eventfactory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MethodInvokingEventFactoryAdapterUnitTest {

	SingleMethodWithExpectedType target = new SingleMethodWithExpectedType();

	@Test
	public void SingleMethod_with_expected_type() {

		final MethodInvokingEventFactoryAdapter<String> adapter = new MethodInvokingEventFactoryAdapter<String>(this.target, String.class);
		assertEquals("some-value", adapter.newInstance());

	}

	private static class SingleMethodWithExpectedType {

		public String newFactory() {
			return "some-value";
		}

	}

}
