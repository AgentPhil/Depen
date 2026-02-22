package dev.philipp.depen;

public class InjectionException extends RuntimeException {

	private static final long serialVersionUID = 3227433202841750573L;

	public InjectionException() {
        super();
    }

    public InjectionException(String msg) {
        super(msg);
    }

    public InjectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InjectionException(Throwable cause) {
        super(cause);
    }
}