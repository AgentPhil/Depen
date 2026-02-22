package dev.philipp.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.Test;

import dev.philipp.depen.CircularDependencyException;
import dev.philipp.depen.Injector;

public class ConstructorInjection {

	@Test
	public void testClassInjectionInConstructor() {
		Injector injector = new Injector();
		injector.provide(StringBuilder.class);
		injector.provide(StringBuilderExample.class);
		StringBuilderExample injected = injector.inject(StringBuilderExample.class);
		assertNotNull(injected);
		assertNotNull(injected.getStringBuilder());
	}
	
	@Test
	public void testValueInjectionInConstructor() {
		Injector injector = new Injector();
		injector.forClass(BigDecimal.class).provideValue(BigDecimal.TEN);
		injector.provide(BigDecimalExample.class);
		BigDecimalExample injected = injector.inject(BigDecimalExample.class);
		assertNotNull(injected);
		assertEquals(BigDecimal.TEN, injected.getBigDecimal());
	}
	
	@Test(expected = CircularDependencyException.class)
	public void testInjectCircle() {
		Injector injector = new Injector();
		injector.provide(CircleAExample.class);
		injector.provide(CircleBExample.class);
		injector.inject(CircleAExample.class);
	}
	
	
	public static class BigDecimalExample {
		
		private BigDecimal bigDecimal;

		public BigDecimalExample(BigDecimal bigDecimal) {
			this.bigDecimal = bigDecimal;
		}
		
		public BigDecimal getBigDecimal() {
			return bigDecimal;
		}
	}
	
	public static class StringBuilderExample {
		
		private StringBuilder stringBuilder;

		public StringBuilderExample(StringBuilder stringBuilder) {
			this.stringBuilder = stringBuilder;
		}
		
		public StringBuilder getStringBuilder() {
			return stringBuilder;
		}
	}
	
	public static class CircleAExample {
		public CircleAExample(CircleBExample b) {
		}
	}
	
	public static class CircleBExample {
		public CircleBExample(CircleAExample stringBuilder) {
		}
	}
}
