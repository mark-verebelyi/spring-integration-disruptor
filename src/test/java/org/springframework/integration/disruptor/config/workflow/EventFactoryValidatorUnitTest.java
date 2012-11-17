package org.springframework.integration.disruptor.config.workflow;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lmax.disruptor.EventFactory;

public class EventFactoryValidatorUnitTest {

	private final EventFactoryValidator validator = new EventFactoryValidator();

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
		assertTrue(this.validator.canProduce(this.stringEventFactory, String.class));
	}

	@Test
	public void Incompatible_types() {
		assertFalse(this.validator.canProduce(this.stringEventFactory, Number.class));
	}

	@Test
	public void Assignable_types() {
		assertTrue(this.validator.canProduce(this.integerEventFactory, Number.class));
	}

	@Test
	public void Not_an_EventFactory() {
		assertFalse(this.validator.canProduce(new Thread(), Integer.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void EventType_can_not_be_null() {
		this.validator.canProduce(this.integerEventFactory, null);
	}

	public void Null_EventFactory_can_not_produce_anything() {
		assertFalse(this.validator.canProduce(null, String.class));
	}

}
