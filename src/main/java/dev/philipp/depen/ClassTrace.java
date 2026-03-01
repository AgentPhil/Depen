package dev.philipp.depen;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * The single purpose of this collection is to detect a circular dependency resolution at runtime.
 * The dependency injection would work without this, the only side effect is time
 */
class ClassTrace {
    private final Deque<Class<?>> stack = new ArrayDeque<>();
    private final Set<Class<?>> set = new HashSet<>();

    public void push(Class<?> value) {
        if (set.add(value)) {
            stack.push(value);
        } else {
        	throw new CircularDependencyException(this, value);        	
        }
    }

    public Class<?> pop() {
    	Class<?> value = stack.pop();
        set.remove(value);
        return value;
    }

    public boolean contains(Class<?> value) {
        return set.contains(value);
    }
	
	@Override
	public String toString() {
		return stack.stream()
        .map(Class::getSimpleName)
        .reduce((a, b) -> a + " -> " + b)
        .orElse("");
	}
}
