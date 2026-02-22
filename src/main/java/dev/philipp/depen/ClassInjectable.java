package dev.philipp.depen;

public class ClassInjectable<T> extends Injectable<T> {

    private final Class<? extends T> clazz;

    ClassInjectable(Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    @Override
    T get() {
        try {
            return this.clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
