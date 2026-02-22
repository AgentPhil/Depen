package dev.philipp.depen;

public class CircularDependencyException extends InjectionException {

	private static final long serialVersionUID = -1811406488899841220L;

	public CircularDependencyException(ClassTrace classTrace, Class<?> value) {
	    super(buildMessage(classTrace, value));
	}

	private static String buildMessage(ClassTrace trace, Class<?> value) {
	    return "Circular dependency detected: " + value.getSimpleName() + " -> " + 
	    		trace.toString();
	}

	
	
}
