package dev.philipp.depen;

import dev.philipp.depen.Injector.ResolutionContext;

public class ValueInjectable<T> extends Injectable<T> {

    private final T value;

    ValueInjectable(T value) {
        this.value = value;
    }

    @Override
    T resolve(ResolutionContext resolutionContext) {
        return this.value;
    }
}
