package org.springframework.integration.disruptor.config.workflow;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lmax.disruptor.EventFactory;

public class EventFactoryValidatorUnitTest {

	private final EventFactoryValidator validator = new EventFactoryValidator();

	@Test
	public void CompatibleTypes() {

		final EventFactory<?> stringEventFactory = new EventFactory<String>() {

			public String newInstance() {
				return null;
			}

		};

		assertTrue(this.validator.canProduce(stringEventFactory, String.class));

	}

}
