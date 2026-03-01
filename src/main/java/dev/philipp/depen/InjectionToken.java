package dev.philipp.depen;

import java.util.Objects;

/**
 * Represents a unique token for dependency injection.
 * <p>
 * An {@code InjectionToken} is used as a key to identify a provided value, class, instance, or factory.
 * Tokens can be scoped by class type or by custom token instance. Only the exact token instance can
 * be used to retrieve its associated dependency when using token-based resolution.
 * </p>
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * // Create a unique token for a type
 * InjectionToken<BigDecimal> token = InjectionToken.create(BigDecimal.class, "MY_TOKEN");
 *
 * Injector injector = new Injector();
 * injector.forToken(token).provideValue(BigDecimal.TEN);
 *
 * BigDecimal value = injector.inject(token); // returns BigDecimal.TEN
 * }</pre>
 *
 * @param <T> the type of object the token refers to
 */
public class InjectionToken<T> {
	
    /**
     * Defines the resolution scope of an injection token.
     */
	enum ResolutionScope {
		CLASS, TOKEN
	}

    private final Class<T> clazz;
    
    private final ResolutionScope scope;
    
    private final String description;
    
    /**
     * Creates a new InjectionToken for token-based resolution.
     * <p>
     * This token acts as a unique key: the value/class/factory provided under this token
     * will only be accessible by injecting using this exact token instance.
     * </p>
     *
     * @param <T> the type of object that will be provided or injected
     * @param clazz the class of the object (e.g., String.class, BigDecimal.class)
     * @param description a human-readable description for debugging or logging
     * @return a new InjectionToken instance that can be used for providing or injecting this type
     */
    public static <T> InjectionToken<T> create(Class<T> clazz, String description) {
    	return new InjectionToken<T>(clazz, ResolutionScope.TOKEN, description);
    }
    
    InjectionToken(Class<T> clazz, ResolutionScope scope) {
        this.clazz = clazz;
        this.scope = scope;
        this.description = "";
    }

    InjectionToken(Class<T> clazz, ResolutionScope scope, String description) {
        this.clazz = clazz;
        this.scope = scope;
        this.description = description;
    }
    
    @Override
    public int hashCode() {
    	if (this.scope == ResolutionScope.CLASS) {
    		return Objects.hash(clazz);        		
    	} else {
    		return super.hashCode();
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj) {
    		return true;
    	}
    	if (obj == null || getClass() != obj.getClass()) {
    		return false;
    	}
    	if (this.scope == ResolutionScope.CLASS) {
    		InjectionToken<?> other = (InjectionToken<?>) obj;
    		return Objects.equals(this.clazz, other.clazz);
    	}
    	return false;
    }
    
    @Override
    public String toString() {
    	if (this.scope == ResolutionScope.CLASS) {
    		return "Class " + this.clazz.getName();    		
    	} else {
    		return "Token " + this.description + " of type " + this.clazz.getName();    		
    	}
    }
}
