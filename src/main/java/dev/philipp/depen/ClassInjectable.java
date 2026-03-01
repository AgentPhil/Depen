package dev.philipp.depen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import dev.philipp.depen.InjectionToken.ResolutionScope;
import dev.philipp.depen.Injector.ResolutionContext;

public class ClassInjectable<T> extends Injectable<T> {

    private final Class<T> clazz;

    ClassInjectable(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    T resolve(ResolutionContext resolutionContext) {
        try {
        	Constructor<T> noParamConstructor = null;
        	@SuppressWarnings("unchecked")
			Constructor<T>[] constructors = (Constructor<T>[]) this.clazz.getConstructors();
			for (Constructor<T> constructor : constructors) {
				if (constructor.getParameterCount() == 0) {
					noParamConstructor = constructor;
				}
			}
			if (noParamConstructor == null && constructors.length != 1) {
				throw new InjectionException("Injectable Classes need a default constructor or only one");
			}
			Constructor<T> constructorOfChoice;
			if (noParamConstructor != null) {
				constructorOfChoice = noParamConstructor;				
			} else {
				constructorOfChoice = constructors[0];				
			}
			Class<?>[] paramTypes = new Class[constructorOfChoice.getParameterCount()];
			Object[] params = new Object[paramTypes.length];
			resolutionContext.classTrace.push(this.clazz);
			for (int i = 0; i < constructorOfChoice.getParameters().length; i++) {
				Parameter parameter = constructorOfChoice.getParameters()[i];
				Class<?> clazz = parameter.getType();
				boolean optional = false;
				Inject injectAnno = parameter.getAnnotation(Inject.class);
				if (injectAnno != null) {
					if (injectAnno.value() != Object.class) {
						clazz = injectAnno.value();
					}
					optional = injectAnno.optional();
				}
				params[i] = resolutionContext.inject(new InjectionToken<>(clazz, ResolutionScope.CLASS), optional, resolutionContext.classTrace);
				resolutionContext.initialize(params[i], resolutionContext);
			}
			T newInstance = constructorOfChoice.newInstance(params);
			resolutionContext.initialize(newInstance, resolutionContext);
			resolutionContext.forClass(this.clazz).provideValue(newInstance);
			resolutionContext.classTrace.pop();
			return newInstance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
