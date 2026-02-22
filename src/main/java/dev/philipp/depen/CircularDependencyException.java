package dev.philipp.depen;

public class CircularDependencyException extends InjectionException {

	private static final long serialVersionUID = -1811406488899841220L;

	public CircularDependencyException(ClassTrace classTrace, Class<?> value) {
	    super(buildMessage(classTrace, value));
	}

	private static String buildMessage(ClassTrace trace, Class<?> value) {
	    String chain = trace.getClasses().stream()
	            .map(Class::getSimpleName)
	            .reduce((a, b) -> a + " -> " + b)
	            .orElse("");

	    return "Circular dependency detected: " +
	           chain + " -> " + value.getSimpleName();
	}

	
	
}
