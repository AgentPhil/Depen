package dev.philipp.depen;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import dev.philipp.depen.InjectionToken.ResolutionScope;

public class InjectorTest {

	@Test
	public void testProvideClass() {
		Injector injector = new Injector();
		InjectionToken<StringBuilder> fileToken = new InjectionToken<>(StringBuilder.class, ResolutionScope.CLASS);
		injector.provide(fileToken, new ClassInjectable<StringBuilder>(StringBuilder.class));
		StringBuilder injected = injector.inject(fileToken);
		assertNotNull(injected);
	}
	
	@Test
	public void testProvideValue() {
		Injector injector = new Injector();
		InjectionToken<BigDecimal> bdToken = new InjectionToken<>(BigDecimal.class, ResolutionScope.CLASS);
		injector.provide(bdToken, new ValueInjectable<BigDecimal>(new BigDecimal(42)));
		BigDecimal injected = injector.inject(bdToken);
		assertNotNull(injected);
		assertEquals(new BigDecimal(42), injected);
	}
	
	@Test
	public void testOptional() {
		Injector injector = new Injector();
		injector.inject(new InjectionToken<>(BigDecimal.class, ResolutionScope.CLASS), true, new ClassTrace());
		
	}
	
	@Test
	public void testProvideClassWithDifferentTokens() {
		Injector injector = new Injector();
		InjectionToken<StringBuilder> fileToken1 = new InjectionToken<>(StringBuilder.class, ResolutionScope.CLASS);
		injector.provide(fileToken1, new ClassInjectable<StringBuilder>(StringBuilder.class));
		InjectionToken<StringBuilder> fileToken2 = new InjectionToken<>(StringBuilder.class, ResolutionScope.CLASS);
		StringBuilder injected = injector.inject(fileToken2);
		assertNotNull(injected);
	}
	
	@Test
	public void testProvideClassWithInstanceResolution() {
		Injector injector = new Injector();
		InjectionToken<StringBuilder> fileToken1 = new InjectionToken<>(StringBuilder.class, ResolutionScope.TOKEN);
		injector.provide(fileToken1, new ClassInjectable<StringBuilder>(StringBuilder.class));
		InjectionToken<StringBuilder> fileToken2 = new InjectionToken<>(StringBuilder.class, ResolutionScope.CLASS);
		StringBuilder wronglyInjected = injector.inject(fileToken2, true, new ClassTrace());
		assertNull(wronglyInjected);
		StringBuilder injected = injector.inject(fileToken1);
		assertNotNull(injected);
	}
	
	@Test
	public void testProvideValueWithInstanceResolution() {
		Injector injector = new Injector();
		InjectionToken<BigDecimal> bdToken1 = new InjectionToken<>(BigDecimal.class, ResolutionScope.TOKEN);
		injector.provide(bdToken1, new ValueInjectable<BigDecimal>(BigDecimal.TEN));
		InjectionToken<BigDecimal> bdToken2 = new InjectionToken<>(BigDecimal.class, ResolutionScope.CLASS);
		BigDecimal wronglyInjected = injector.inject(bdToken2, true, new ClassTrace());
		assertNull(wronglyInjected);
		BigDecimal injected = injector.inject(bdToken1);
		assertEquals(BigDecimal.TEN, injected);
	}
	
	@Test
	public void testSelfInjection() throws Exception {
		Injector injector = new Injector();
		Injector injector2 = injector.inject(new InjectionToken<>(Injector.class, ResolutionScope.CLASS));
		assertEquals(injector, injector2);
	}
	
	@Test(expected = InjectionException.class)
	public void testNoValueProvided() throws Exception {
		Injector injector = new Injector();
		injector.inject(new InjectionToken<>(StringBuilder.class, ResolutionScope.CLASS));
	}
	

}
