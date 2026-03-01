package dev.philipp.depen;

import java.util.function.Function;

import dev.philipp.depen.Injector.ResolutionContext;

class FactoryInjectable<T> extends Injectable<T> {

	private Function<Injector, ? extends T> factory;

	public FactoryInjectable(Function<Injector, ? extends T> factory) {
		this.factory = factory;
	}
	
	@Override
	T resolve(ResolutionContext resolutionContext) {
		return this.factory.apply(resolutionContext);
	}

}
