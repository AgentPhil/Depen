package dev.philipp.depen;

import java.lang.reflect.Field;
import java.util.HashMap;
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
    
    <T> void provide(InjectionToken<T> token, Injectable<T> injectable) {
	    this.injectables.put(token, injectable);
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
    
    public void initialize(Object object) {
    	this.initialize(object, new ResolutionContext(new ClassTrace()));
    }
    
    void initialize(Object object, ResolutionContext resolutionContext) {
    	if (object == null) {
    		return;
    	}
    	for (Field field : object.getClass().getDeclaredFields()) {
			Inject injectAnno = field.getAnnotation(Inject.class);
			if (injectAnno == null) {
				continue;
			}
			resolutionContext.classTrace.push(field.getClass());
			field.setAccessible(true);
			Object injected = inject(new InjectionToken<>(field.getType(), ResolutionScope.CLASS), false, resolutionContext.classTrace);
			try {
				field.set(object, injected);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InjectionException(e);
			}
			resolutionContext.classTrace.pop();
		}
	}

	public class InjectionPoint<T> {
    	
    	private final InjectionToken<T> token;
    	
    	InjectionPoint(InjectionToken<T> token) {
			this.token = token;
    	}
    	
    	@SuppressWarnings({ "rawtypes", "unchecked" })
		public void provideClass(Class<? extends T> clazz) {
    		Injector.this.provide(this.token, new ClassInjectable(clazz));
    	}
    	
    	public void provideValue(T value) {
    		Injector.this.provide(this.token, new ValueInjectable<T>(value));
    	}
    	
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		public void provideInstanceOf(Class<? extends T> clazz) {
    		Injector.this.provide(this.token, new InstanceInjectable(clazz));
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
