package com.github.dynamicextensionsalfresco.osgi;

import java.util.Collection;

/**
 * Created by jasper on 17/07/17.
 */
public interface DependencyMetadataProvider<T> {
    public boolean allowCircularReferences();
    Collection<Object> imports(T item);
    Collection<Object> exports(T item);
}
