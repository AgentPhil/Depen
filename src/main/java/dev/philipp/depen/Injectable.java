package dev.philipp.depen;

import dev.philipp.depen.Injector.ResolutionContext;

abstract class Injectable<T> {

    abstract T resolve(ResolutionContext resolutionContext);
}
