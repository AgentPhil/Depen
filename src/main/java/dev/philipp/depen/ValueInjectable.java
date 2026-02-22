package dev.philipp.depen;

public class ValueInjectable<T> extends Injectable<T> {

    private final T value;

    ValueInjectable(T value) {
        this.value = value;
    }

    @Override
    T get() {
        return this.value;
    }
}
