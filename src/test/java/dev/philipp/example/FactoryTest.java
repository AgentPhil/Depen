package dev.philipp.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import dev.philipp.depen.InjectionException;
import dev.philipp.depen.Injector;

public class FactoryTest {
    @Test
    public void testFactoryCreatesInstance() {
        Injector injector = new Injector();
        injector.forClass(StringBuilder.class)
                .provideFactory(i -> new StringBuilder("fromFactory"));
        StringBuilder injected = injector.inject(StringBuilder.class);
        assertNotNull(injected);
        assertEquals("fromFactory", injected.toString());
    }

    @Test
    public void testFactoryReceivesInjector() {
        Injector injector = new Injector();
        injector.forClass(BigDecimal.class).provideValue(BigDecimal.TEN);;
        injector.forClass(StringBuilder.class)
                .provideFactory(i -> {
                    // can use injector to inject dependencies
                    BigDecimal bd = i.inject(BigDecimal.class);
                    return new StringBuilder(bd.toString());
                });
        StringBuilder injected = injector.inject(StringBuilder.class);
        assertEquals(BigDecimal.TEN.toString(), injected.toString());
    }

    @Test
    public void testFactoryProducesNewInstanceEachTime() {
        Injector injector = new Injector();
        AtomicInteger counter = new AtomicInteger();
        injector.forClass(StringBuilder.class)
                .provideFactory(i -> new StringBuilder("id:" + counter.getAndIncrement()));
        
        StringBuilder first = injector.inject(StringBuilder.class);
        StringBuilder second = injector.inject(StringBuilder.class);

        assertNotEquals(first.toString(), second.toString());
        assertEquals("id:0", first.toString());
        assertEquals("id:1", second.toString());
    }

    @Test(expected = InjectionException.class)
    public void testFactoryNotProvidedThrows() {
        Injector injector = new Injector();
        injector.inject(StringBuilder.class);
    }

    @Test
    public void testFactoryCanInjectOptional() {
        Injector injector = new Injector();
        injector.forClass(StringBuilder.class)
                .provideFactory(i -> {
                    // optional injection returns null
                    BigDecimal optional = i.injectOptional(BigDecimal.class);
                    return new StringBuilder(optional == null ? "null" : optional.toString());
                });
        StringBuilder injected = injector.inject(StringBuilder.class);
        assertEquals("null", injected.toString());
    }
}
