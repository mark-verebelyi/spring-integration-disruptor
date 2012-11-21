package org.springframework.integration.disruptor.config.workflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lmax.disruptor.EventFactory;

public class EventFactoryValidatorUnitTest {

	private final EventFactory<?> stringEventFactory = new EventFactory<String>() {

		public String newInstance() {
			return "foobar";
		}

	};

	private final EventFactory<?> integerEventFactory = new EventFactory<Integer>() {

		public Integer newInstance() {
			return 9;
		}

	};

	@Test
	public void Compatible_types() {
		final EventFactoryFactory<String> factory = new EventFactoryFactory<String>();
		factory.setEventType(String.class);
		assertTrue(factory.isNativeEventFactory(this.stringEventFactory));
	}

	@Test
	public void Incompatible_types() {
		final EventFactoryFactory<Number> factory = new EventFactoryFactory<Number>();
		factory.setEventType(Number.class);
		assertFalse(factory.isNativeEventFactory(this.stringEventFactory));
	}

	@Test
	public void Assignable_types() {
		final EventFactoryFactory<Number> factory = new EventFactoryFactory<Number>();
		factory.setEventType(Number.class);
		assertTrue(factory.isNativeEventFactory(this.integerEventFactory));
	}

	@Test
	public void Not_an_EventFactory() {
		final EventFactoryFactory<String> factory = new EventFactoryFactory<String>();
		factory.setEventType(String.class);
		assertFalse(factory.isNativeEventFactory(new Thread()));
	}

	public void Null_EventFactory_can_not_produce_anything() {
		final EventFactoryFactory<String> factory = new EventFactoryFactory<String>();
		factory.setEventType(String.class);
		assertFalse(factory.isNativeEventFactory(null));
	}

}
