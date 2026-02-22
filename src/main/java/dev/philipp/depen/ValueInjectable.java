package dev.philipp.depen;

public class ValueInjectable<T> extends Injectable<T> {

    private final T value;

    public ValueInjectable(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return this.value;
    }
}
