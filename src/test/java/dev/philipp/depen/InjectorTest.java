package dev.philipp.depen;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import dev.philipp.depen.InjectionToken.ResolutionScope;

public class InjectorTest {

	@Test
	public void testProvideClass() {
		Injector injector = new Injector();
		InjectionToken<StringBuilder> fileToken = new InjectionToken<StringBuilder>(StringBuilder.class, ResolutionScope.CLASS);
		injector.provide(fileToken, new ClassInjectable<StringBuilder>(StringBuilder.class));
		StringBuilder injected = injector.inject(fileToken);
		assertNotNull(injected);
	}
	
	@Test
	public void testProvideValue() {
		Injector injector = new Injector();
		InjectionToken<BigDecimal> bdToken = new InjectionToken<BigDecimal>(BigDecimal.class, ResolutionScope.CLASS);
		injector.provide(bdToken, new ValueInjectable<BigDecimal>(new BigDecimal(42)));
		BigDecimal injected = injector.inject(bdToken);
		assertNotNull(injected);
		assertEquals(new BigDecimal(42), injected);
	}
	
	@Test
	public void testProvideClassWithDifferentTokens() {
		Injector injector = new Injector();
		InjectionToken<StringBuilder> fileToken1 = new InjectionToken<StringBuilder>(StringBuilder.class, ResolutionScope.CLASS);
		injector.provide(fileToken1, new ClassInjectable<StringBuilder>(StringBuilder.class));
		InjectionToken<StringBuilder> fileToken2 = new InjectionToken<StringBuilder>(StringBuilder.class, ResolutionScope.CLASS);
		StringBuilder injected = injector.inject(fileToken2);
		assertNotNull(injected);
	}

}
