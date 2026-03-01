package dev.philipp.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import dev.philipp.depen.Inject;
import dev.philipp.depen.InjectionException;
import dev.philipp.depen.Injector;
import dev.philipp.example.AnnotationInjection.App2;
import dev.philipp.example.AnnotationInjection.Service;

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
		injector.forClass(Service.class).provideValue(service);
		App1 app1 = new App1();
		injector.initialize(app1);
		assertEquals(service, app1.service);
	}
	
	@Test
	public void testInstanceInjection() {
		Injector injector = new Injector();
		injector.forClass(Service.class).provideInstanceOf(Service.class);
		App1 app1 = new App1();
		injector.initialize(app1);
		Service service = injector.inject(Service.class);
		assertEquals(service, app1.service);
	}
	
	@Test(expected = InjectionException.class)
	public void testServiceNotProvided() {
		Injector injector = new Injector();
		App1 app1 = new App1();
		injector.initialize(app1);
	}
	
	@Test
	public void testMixedInjection() {
		Injector injector = new Injector();
		injector.forClass(Service.class).provideInstanceOf(Service.class);
		Service service = injector.inject(Service.class);
		injector.provide(App1.class);
		injector.provide(App2.class);
		App2 app2 = injector.inject(App2.class);
		assertEquals(service, app2.service);
		assertEquals(service, app2.app1.service);
	}
	
	
	public static class Service {
		
	}
	
	public static class App1 {
		@Inject
		private Service service;
	}
	
	public static class App2 {
		@Inject
		private Service service;
		
		private App1 app1;
		
		public App2(App1 app1) {
			this.app1 = app1;
		}
	}
	
}
