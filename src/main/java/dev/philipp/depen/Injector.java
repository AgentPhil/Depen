package dev.philipp.depen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.philipp.depen.InjectionToken.ResolutionScope;

public class Injector {
    protected Map<InjectionToken<?>, Injectable<?>> injectables = new HashMap<>();
    
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
    
    public <T> T inject(Class<T> clazz) {
    	return this.inject(new InjectionToken<>(clazz, ResolutionScope.CLASS));
    }
    
    public <T> T injectOptional(Class<T> clazz) {
    	return this.inject(new InjectionToken<>(clazz, ResolutionScope.CLASS), true,new ClassTrace());
    }
    
    public <T> T inject(InjectionToken<T> token) {
    	return this.inject(token, false, new ClassTrace());
    }
    
    public <T> T injectOptional(InjectionToken<T> token) {
    	return this.inject(token, true, new ClassTrace());
    }
    
    <T> void provide(InjectionToken<T> token, Injectable<T> injectable) {
        this.injectables.put(token, injectable);
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
        		throw new InjectionException(token.toString() + " not provided");        		
        	}
        }
		return (T) injectable.resolve(new ResolutionContext(classTrace));
    }
    
    public class InjectionPoint<T> {
    	
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
    
    class ResolutionContext extends Injector {
    	
    	ClassTrace classTrace = new ClassTrace();
    	
    	ResolutionContext(ClassTrace classTrace) {
    		this.classTrace = classTrace;
		}
    	
    	@SuppressWarnings("unchecked")
		@Override
    	<T> T inject(InjectionToken<T> token, boolean optional, ClassTrace classTrace) {
    		Injectable<?> injectable = this.injectables.get(token);
    		if (injectable != null) {
    			return (T) injectable.resolve(this);
    		}
    		return Injector.this.inject(token, optional, classTrace);
    	}
    }
}
