package dev.philipp.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import dev.philipp.depen.Inject;
import dev.philipp.depen.Injector;

public class AnnotationInjection {

	
	@Test
	public void testAnnotationInjection() {
		Injector injector = new Injector();
		injector.provide(Service.class);
		App1 app1 = new App1();
		injector.initialize(app1);
		assertNotNull(app1.service);
	}
	
	@Test
	public void testValueInjection() {
		Injector injector = new Injector();
		Service service = new Service();
		injector.forClass(Service.class).provideValue(service);;
		App1 app1 = new App1();
		injector.initialize(app1);
		assertEquals(service, app1.service);
	}
	
	
	
	public static class Service {
		
	}
	
	public static class App1 {
		@Inject
		private Service service;
	}
	
}
