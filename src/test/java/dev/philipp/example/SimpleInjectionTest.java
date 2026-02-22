package dev.philipp.example;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import dev.philipp.depen.InjectionException;
import dev.philipp.depen.InjectionToken;
import dev.philipp.depen.Injector;

public class SimpleInjectionTest {

	@Test
	public void testSimpleClassInjection() {
		Injector injector = new Injector();
		injector.provide(StringBuilder.class);
		StringBuilder stringBuilder = injector.inject(StringBuilder.class);
		assertNotNull(stringBuilder);
	}
	
	@Test(expected = InjectionException.class)
	public void testInjectionException() {
		Injector injector = new Injector();
		injector.inject(StringBuilder.class);
	}
	
	@Test
	public void testInjectOptional() {
		Injector injector = new Injector();
		assertNull(injector.injectOptional(StringBuilder.class));
	}
	
	@Test
	public void testProvideValue() {
		Injector injector = new Injector();
		injector.forClass(BigDecimal.class).provideValue(BigDecimal.TEN);
		BigDecimal injected = injector.inject(BigDecimal.class);
		assertEquals(BigDecimal.TEN, injected);
	}
	
	@Test
	public void testProvideInheritedClass() {
		Injector injector = new Injector();
		injector.forClass(List.class).provideClass(ArrayList.class);
		List<?> injected = injector.inject(List.class);
		assertEquals(ArrayList.class, injected.getClass());
	}
	
	@Test
	public void testInjectionToken() {
		Injector injector = new Injector();
		InjectionToken<BigDecimal> myToken = InjectionToken.create(BigDecimal.class, "MY_TOKEN");
		injector.forToken(myToken).provideValue(BigDecimal.TEN);
		BigDecimal injected = injector.inject(myToken);
		assertEquals(BigDecimal.TEN, injected);
	}
	
	@Test
	public void testDuplicateInjectionToken() {
		Injector injector = new Injector();
		InjectionToken<BigDecimal> myToken = InjectionToken.create(BigDecimal.class, "MY_TOKEN");
		injector.forToken(myToken).provideValue(BigDecimal.TEN);
		InjectionToken<BigDecimal> mySecondToken = InjectionToken.create(BigDecimal.class, "MY_TOKEN");
		BigDecimal injected = injector.injectOptional(mySecondToken);
		assertNull(injected);
	}
	
	@Test(expected = InjectionException.class)
	public void testNoTokenInjectionException() {
		Injector injector = new Injector();
		InjectionToken<BigDecimal> myToken = InjectionToken.create(BigDecimal.class, "MY_TOKEN");
		injector.inject(myToken);
	}
	
}
