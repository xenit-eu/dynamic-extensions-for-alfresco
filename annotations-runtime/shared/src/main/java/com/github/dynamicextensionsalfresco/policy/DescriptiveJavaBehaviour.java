package com.github.dynamicextensionsalfresco.policy;

import java.lang.reflect.Method;
import kotlin.jvm.internal.Intrinsics;
import org.alfresco.repo.policy.JavaBehaviour;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simplify toString to method signature
 *
 * @author Laurent Van der Linden
 */
public final class DescriptiveJavaBehaviour extends JavaBehaviour {

    private final Method methodReference;

    public DescriptiveJavaBehaviour(@Nullable Object instance, @NotNull Method methodReference,
            @Nullable NotificationFrequency frequency) {
        super(instance, methodReference.getName(), frequency);

        Intrinsics.checkParameterIsNotNull(methodReference, "methodReference");
        this.methodReference = methodReference;
    }

    @NotNull
    public String toString() {
        return methodReference.getDeclaringClass().getName() + "." + methodReference.getName();
    }
}
