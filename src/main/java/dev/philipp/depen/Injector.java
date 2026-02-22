package dev.philipp.depen;

import java.util.HashMap;
import java.util.Map;

public class Injector {
    private Map<InjectionToken<?>, Injectable<?>> injectables = new HashMap<>();

    public <T> void provide(InjectionToken<T> token, Injectable<T> injectable) {
        this.injectables.put(token, injectable);
    }

    @SuppressWarnings("unchecked")
    <T> T inject(InjectionToken<T> token) {
        Injectable<?> injectable = this.injectables.get(token);
        if (injectable == null) {
        }
		return (T) injectable.get();
    }
}
