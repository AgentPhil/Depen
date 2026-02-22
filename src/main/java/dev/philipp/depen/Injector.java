package dev.philipp.depen;

import java.util.HashMap;
import java.util.Map;

import dev.philipp.depen.InjectionToken.ResolutionScope;

public class Injector {
    private Map<InjectionToken<?>, Injectable<?>> injectables = new HashMap<>();
    
    public Injector() {
		this.forClass(Injector.class).provideValue(this);
	}

    public <T> InjectionPoint<T> forClass(Class<T> classScopedToken) {
    	return this.new InjectionPoint<T>(new InjectionToken<T>(classScopedToken, ResolutionScope.CLASS));
    }
    
    public <T> InjectionPoint<T> forToken(InjectionToken<T> instanceScopedToken) {
    	return this.new InjectionPoint<T>(instanceScopedToken);
    }
    
    public <T> void provide(Class<T> clazz) {
    	this.forClass(clazz).provideClass(clazz);
    }
    
    <T> void provide(InjectionToken<T> token, Injectable<T> injectable) {
        this.injectables.put(token, injectable);
    }

    @SuppressWarnings("unchecked")
    <T> T inject(InjectionToken<T> token) {
    	if (token == null) {
    		throw new IllegalArgumentException("Null-Token not possible");
    	}
        Injectable<?> injectable = this.injectables.get(token);
        if (injectable == null) {
        	throw new InjectionException(token.toString() + " not provided");
        }
		return (T) injectable.get();
    }
    
    @SuppressWarnings("unchecked")
    <T> T injectOptional(InjectionToken<T> token) {
        Injectable<?> injectable = this.injectables.get(token);
        if (injectable == null) {
        	return null;
        }
		return (T) injectable.get();
    }
    
    class InjectionPoint<T> {
    	
    	private final InjectionToken<T> token;
    	
    	public InjectionPoint(InjectionToken<T> token) {
			this.token = token;
    	}
    	
    	public void provideClass(Class<? extends T> clazz) {
    		Injector.this.provide(this.token, new ClassInjectable<T>(clazz));
    	}
    	
    	public void provideValue(T value) {
    		Injector.this.provide(this.token, new ValueInjectable<T>(value));
    	}
    }
}
