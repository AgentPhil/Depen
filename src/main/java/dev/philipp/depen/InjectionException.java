package dev.philipp.depen;

public class InjectionException extends RuntimeException {

	private static final long serialVersionUID = 3227433202841750573L;

	InjectionException() {
        super();
    }

    InjectionException(String msg) {
        super(msg);
    }

    InjectionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    InjectionException(Throwable cause) {
        super(cause);
    }
    
    InjectionException(String msg, Throwable cause, ClassTrace classTrace) {
        super(msg + ": " + classTrace.toString(), cause);
    }

	public InjectionException(String msg, ClassTrace classTrace) {
        super(msg + ": " + classTrace.toString());
	}
    
    
}