# Depen – Lightweight Java Dependency Injection

### A simple and flexible dependency injection container for Java supporting class-based, token-based, factory, singleton, and value injections. Also supports field and constructor injection, optional dependencies, and class-based multi-provision via annotations.

Features

- Class-based injection – automatically instantiate and inject classes.
- Token-based injection – provide unique tokens for specific dependencies.
- Singleton and instance injection – define a single instance per type.
- Factory-based injection – create dependencies dynamically using a Function.
- Value injection – inject pre-created objects or constants for a class or token.
- Field and constructor injection – supports @Inject annotation for automatic wiring.
- Optional injection – inject nullable dependencies.
- Multi-provision with @Provide – inject multiple classes automatically, e.g., Parent–Child graphs.
- Circular dependency detection – prevents infinite loops during injection.

### Installation

Include the source in your project. No external dependencies are required.

### Usage
1. Class-based Injection
```java
Injector injector = new Injector();
injector.provide(StringBuilder.class);

StringBuilder sb = injector.inject(StringBuilder.class);
System.out.println(sb); // prints empty StringBuilder instance
```
2. Constructor Injection
```java
public class ServiceUser {
    private final Service service;
    public ServiceUser(Service service) { this.service = service; }
}

ServiceUser user = injector.inject(ServiceUser.class); // service automatically injected
```
3. Field Injection
```java
public class FieldApp {
    @Inject
    Service service;
}

Injector injector = new Injector();
injector.provide(Service.class);

//initialize yourself
FieldApp app = new FieldApp();
injector.initialize(app); // service field is injected
//or
injector.provide(FieldApp.class);
injector.inject(FieldApp.class)
```
4. Singleton / Instance Injection
```java
injector.forClass(Service.class)
        .provideInstanceOf(Service.class);

Service s = injector.inject(Service.class);
```
5. Factory Injection
```java
injector.forClass(StringBuilder.class)
        .provideFactory(i -> new StringBuilder("from factory"));

StringBuilder s1 = injector.inject(StringBuilder.class);
```
6. Token-based Injection
```java
InjectionToken<BigDecimal> token = InjectionToken.create(BigDecimal.class, "MY_TOKEN");
injector.forToken(token).provideValue(BigDecimal.TEN);

BigDecimal value = injector.inject(token); //the injector can be injected for that purpose
```
7. Optional Injection
```
Service optional = injector.injectOptional(Service.class); // returns null if not provided
```
9. Multi-provision with @Provide
```java
@Provide(Child1.class)
@Provide(Child2.class)
public class Parent {
    @Inject Child1 child1;
    @Inject Child2 child2;
}

public class Child1 { @Inject Parent parent; }
public class Child2 { @Inject Parent parent; }
//Both children receive the same parent instance
```

### Detects circular dependencies at runtime
This example will throw a CircularDependencyException, as soon as one of them is injected. 
```java
public static class A {
    @Inject B b;
}
public static class B {
    @Inject C c;
}
public static class C {
    @Inject D d;
}
public static class D {
    @Inject A a;
}
```
### Breaking Circular Dependencies
You can pre-create instances to break circular dependencies:
```java
Injector injector = new Injector();
Parent parent = new Parent();
injector.forClass(Parent.class).provideValue(parent);
injector.provide(Child.class);
injector.initialize(parent);
```
License

MIT License © 2026
