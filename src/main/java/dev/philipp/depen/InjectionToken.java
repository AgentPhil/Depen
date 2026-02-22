package dev.philipp.depen;

import java.util.Objects;

public class InjectionToken<T> {
	
	enum ResolutionScope {
		CLASS, INSTANCE
	}

    private final Class<T> clazz;
    
    private final ResolutionScope scope;
    
    private final String description;
    
    /**
     * Creates a new Token for token-instance based resolution. The provided value/class will only be accessible by
     * providing this exact instance of the InjectionToken
     * @param <T> type of the provided value
     * @param clazz class of the provided value, String is allowed
     * @return
     */
    public static <T> InjectionToken<T> create(Class<T> clazz, String description) {
    	return new InjectionToken<T>(clazz, ResolutionScope.INSTANCE, description);
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
