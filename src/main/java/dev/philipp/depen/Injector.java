package dev.philipp.depen;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import dev.philipp.depen.InjectionToken.ResolutionScope;

/**
 * <p>
 * The {@code Injector} is a flexible dependency injection container that supports multiple binding types:
 * </p>
 * <ul>
 *     <li>Class-based injection</li>
 *     <li>Token-based injection</li>
 *     <li>Singleton instance injection</li>
 *     <li>Factory-based injection</li>
 *     <li>Value injection</li>
 * </ul>
 * <p>
 * It also supports:
 * <ul>
 *     <li>Field-based injection with {@link dev.philipp.depen.Inject}</li>
 *     <li>Constructor injection, automatically resolving constructor parameters</li>
 *     <li>Class-based multi-provision using {@link dev.philipp.depen.Provide}</li>
 *     <li>Optional injection, where a dependency may be null if not provided</li>
 * </ul>
 * </p>
 * <p><b>Direct usage examples:</b></p>
 * 
 * <p><b>Constructor injection:</b></p>
 * <pre>{@code
 * public class ServiceUser {
 *     private final Service service;
 *     public ServiceUser(Service service) {
 *         this.service = service;
 *     }
 * }
 * 
 * ServiceUser user = injector.inject(ServiceUser.class); // Service is automatically injected
 * }</pre>
 * 
 * <p><b>Field injection:</b></p>
 * <pre>{@code
 * public class FieldApp {
 *     @Inject
 *     Service service;
 * }
 * 
 * FieldApp app = new FieldApp();
 * injector.initialize(app); // service field is injected
 * }</pre>
 * 
 * <p><b>Manually injecting fields:</b></p>
 * <pre>{@code
 * Parent parent = new Parent(); // manually create to break circle
 * injector.initialize(parent); //injects all annotated fields
 * injector.forClass(Parent.class).provideValue(parent); //common
 * }</pre>
 * 
 * <p><b>Optional injection:</b></p>
 * <pre>{@code
 * Service optionalService = injector.injectOptional(Service.class); // returns null if not provided
 * }</pre>
 * 
 * <p><b>Class-based multi-provision with @Provide:</b></p>
 * <pre>{@code
 * @Provide(Child1.class)
 * @Provide(Child2.class)
 * public class Parent {
 *     @Inject Child1 child1;
 *     @Inject Child2 child2;
 * }
 * 
 * public class Child1 { @Inject Parent parent; }
 * public class Child2 { @Inject Parent parent; }
 * 
 * }</pre>
 * 
 */
public class Injector {
    protected Map<InjectionToken<?>, Injectable<?>> injectables = new HashMap<>();
    
    /**
     * Creates a new Injector and automatically provides itself as an injectable.
     */
    public Injector() {
		this.forClass(Injector.class).provideValue(this);
	}

    /**
     * Returns an InjectionPoint for a given class type, which can then be used to provide
     * a class, instance, value, or factory.
     *
     * @param token The class to use as the token.
     * @return an InjectionPoint to define the binding.
     */
    public <T> InjectionPoint<T> forClass(Class<T> token) {
    	return this.new InjectionPoint<T>(new InjectionToken<T>(token, ResolutionScope.CLASS));
    }
    
    /**
     * Returns an InjectionPoint for a custom InjectionToken.
     *
     * @param token the InjectionToken to provide for
     * @return an InjectionPoint to define the binding
     */
    public <T> InjectionPoint<T> forToken(InjectionToken<T> token) {
    	return this.new InjectionPoint<T>(token);
    }
    
    /**
     * Provides a class for its own type. 
     * <p><b>Be careful: </b>the Injector will create a new instance every time, #inject is called</p>
     * <br>Equivalent to
     * {@code forClass(clazz).provideClass(clazz)}.
     *
     * @param clazz the class to provide
     */
    public <T> void provide(Class<T> clazz) {
    	this.forClass(clazz).provideClass(clazz);
    }
    
    <T> void provide(InjectionToken<T> token, Injectable<T> injectable) {
	    this.injectables.put(token, injectable);
	}
    
    /**
     * Injects an instance of the given class type.
     *
     * @param clazz the class to inject
     * @return an instance of the class
     * @throws InjectionException if the class has not been provided
     */
	public <T> T inject(Class<T> clazz) {
    	return this.inject(new InjectionToken<>(clazz, ResolutionScope.CLASS));
    }
    
    /**
     * Injects an instance of the given class type, or returns null if not provided.
     *
     * @param clazz the class to inject optionally
     * @return an instance or null
     */
    public <T> T injectOptional(Class<T> clazz) {
    	return this.inject(new InjectionToken<>(clazz, ResolutionScope.CLASS), true,new ClassTrace());
    }
    
    /**
     * Injects an instance using a custom InjectionToken.
     *
     * @param token the injection token
     * @return the resolved instance
     * @throws InjectionException if not provided
     */
    public <T> T inject(InjectionToken<T> token) {
    	return this.inject(token, false, new ClassTrace());
    }
    
    /**
     * Injects an optional instance using a custom InjectionToken.
     *
     * @param token the injection token
     * @return the resolved instance or null if not provided
     */
    public <T> T injectOptional(InjectionToken<T> token) {
    	return this.inject(token, true, new ClassTrace());
    }
        
    @SuppressWarnings("unchecked")
    <T> T inject(InjectionToken<T> token, boolean optional, ClassTrace classTrace) {
    	if (token == null) {
    		throw new IllegalArgumentException("Null-Token not possible");
    	}
        Injectable<?> injectable = this.injectables.get(token);
        if (injectable == null) {
        	if (optional) {
        		return null;
        	} else {
        		throw new InjectionException(token.toString() + " not provided", classTrace);        		
        	}
        }
		return (T) injectable.resolve(new ResolutionContext(classTrace));
    }
    
    /**
     * Initializes an object by injecting fields annotated with @Inject and
     * processing any @Provide annotations on the object's class.
     *
     * @param object the object to initialize
     */
    public void initialize(Object object) {
    	this.initialize(object, new ResolutionContext(new ClassTrace()));
    }
    
    void initialize(Object object, ResolutionContext resolutionContext) {
    	if (object == null) {
    		return;
    	}
    	Provide[] provides = object.getClass().getAnnotationsByType(Provide.class);
    	for (Provide provide : provides) {
    		resolutionContext.provide(provide.value());
    	}
    	resolutionContext.classTrace.push(object.getClass());
    	for (Field field : object.getClass().getDeclaredFields()) {
			Inject inject = field.getAnnotation(Inject.class);
			if (inject == null) {
				continue;
			}
			field.setAccessible(true);
			Class<?> clazz;
			if (inject.value() != Object.class) {
				clazz = inject.value();
			} else {
				clazz = field.getType();
			}
			Object injected = inject(new InjectionToken<>(clazz, ResolutionScope.CLASS), inject.optional(), resolutionContext.classTrace);
			try {
				field.set(object, injected);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InjectionException(e);
			}
		}
    	resolutionContext.classTrace.pop();
	}

    /**
     * Represents a binding for a class or token inside an {@link Injector}.
     * <p>
     * Use {@link InjectionPoint} to define how a type should be provided: by class, instance, value, or factory.
     * </p>
     * <p><b>Example usage:</b></p>
     * <pre>{@code
     * //new instance every time
     * injector.forClass(StringBuilder.class).provideClass(StringBuilder.class);
     *
     * // Bind singleton instance
     * injector.forClass(Service.class).provideInstanceOf(Service.class);
     *
     * // Bind constant value
     * injector.forClass(BigDecimal.class).provideValue(BigDecimal.TEN);
     *
     * // Bind factory
     * injector.forClass(StringBuilder.class)
     *         .provideFactory(i -> new StringBuilder("from factory"));
     * }</pre>
     *
     * @param <T> the type of object being provided
     */
	public class InjectionPoint<T> {
    	
    	private final InjectionToken<T> token;
    	
    	InjectionPoint(InjectionToken<T> token) {
			this.token = token;
    	}
    	
        /**
         * Provides a class to create a new instance each injection.
         * The class must have a constructor with no parameters or only one constructor
         *
         * @param clazz the class to instantiate for injection
         */
    	@SuppressWarnings({ "rawtypes", "unchecked" })
		public void provideClass(Class<? extends T> clazz) {
    		Injector.this.provide(this.token, new ClassInjectable(clazz));
    	}
    	
        /**
         * Provides a fixed value to be injected.
         *
         * @param value the value to inject
         */
    	public void provideValue(T value) {
    		Injector.this.provide(this.token, new ValueInjectable<T>(value));
    	}
    	
        /**
         * Provides a singleton instance inside the injection scope for the given class type.
         * The class must have a constructor with no parameters or only one constructor
         *
         * @param clazz the class to instantiate once
         */
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public void provideInstanceOf(Class<? extends T> clazz) {
    		Injector.this.provide(this.token, new InstanceInjectable(clazz));
    	}
    	
        /**
         * Provides a factory function that creates a new instance for each injection.
         *
         * @param factory the factory function that receives the current Injector
         */
		public void provideFactory(Function<Injector, ? extends T> factory) {
    		Injector.this.provide(this.token, new FactoryInjectable<>(factory));
    	}
    }
    
    class ResolutionContext extends Injector {
    	
    	ClassTrace classTrace;
    	
    	ResolutionContext(ClassTrace classTrace) {
    		this.classTrace = classTrace;
		}
    	
    	@SuppressWarnings("unchecked")
		@Override
    	<T> T inject(InjectionToken<T> token, boolean optional, ClassTrace classTrace) {
    		Injectable<?> injectable = this.injectables.get(token);
    		if (injectable != null) {
    			return (T) injectable.resolve(new ResolutionContext(classTrace));
    		}
    		return Injector.this.inject(token, optional, classTrace);
    	}
    }
}
