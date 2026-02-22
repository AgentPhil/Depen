package dev.philipp.depen;

import java.util.Objects;

public class InjectionToken<T> {
	
	enum ResolutionScope {
		CLASS, INSTANCE
	}

    private final Class<T> clazz;
    
    private final ResolutionScope scope;

    public InjectionToken(Class<T> clazz, ResolutionScope scope) {
        this.clazz = clazz;
        this.scope = scope;
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
}
