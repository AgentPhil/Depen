package dev.philipp.depen;

public class ClassInjectable<T> extends Injectable<T> {

    private final Class<T> clazz;

    public ClassInjectable(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T get() {
        try {
            return this.clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
