package dev.philipp.depen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import dev.philipp.depen.InjectionToken.ResolutionScope;
import dev.philipp.depen.Injector.ResolutionContext;

public class ClassInjectable<T> extends Injectable<T> {

    private final Class<? extends T> clazz;

    ClassInjectable(Class<? extends T> clazz) {
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
			if (noParamConstructor != null) {
				return this.clazz.getConstructor().newInstance();				
			}
			//include in Cycle
			Constructor<T> customConstructor = constructors[0];
			Class<?>[] paramTypes = new Class[customConstructor.getParameterCount()];
			Object[] params = new Object[paramTypes.length];
			for (int i = 0; i < customConstructor.getParameters().length; i++) {
				Parameter parameter = customConstructor.getParameters()[i];
				resolutionContext.classTrace.push(parameter.getType());
				params[i] = resolutionContext.inject(new InjectionToken<>(parameter.getType(), ResolutionScope.CLASS), false, resolutionContext.classTrace);
				resolutionContext.classTrace.pop();
			}
			T newInstance = customConstructor.newInstance(params);
			//injectionCycle.provide(new Inje, injectable);
			//TODO inject Annotation based stuff
        	//TODO Annotation at parameter?
			return newInstance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
