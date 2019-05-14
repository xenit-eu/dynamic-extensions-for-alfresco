package com.github.dynamicextensionsalfresco.gradle.internal.rest;

import java.util.function.Consumer;

@FunctionalInterface
interface ThrowingConsumer<T, X extends Throwable>  extends Consumer<T> {
    void throwingAccept(T var1) throws X;

    default void accept(T var1) {
        try {
            throwingAccept(var1);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
