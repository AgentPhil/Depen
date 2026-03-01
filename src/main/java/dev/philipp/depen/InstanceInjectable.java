package dev.philipp.depen;

import dev.philipp.depen.Injector.ResolutionContext;

public class InstanceInjectable <T> extends ClassInjectable<T> {

	private T instance = null;
	
	InstanceInjectable(Class<T> clazz) {
		super(clazz);
	}
	
	@Override
	T resolve(ResolutionContext resolutionContext) {
		if (instance != null) {
			return instance;
		}
		return super.resolve(resolutionContext);
	}
}
