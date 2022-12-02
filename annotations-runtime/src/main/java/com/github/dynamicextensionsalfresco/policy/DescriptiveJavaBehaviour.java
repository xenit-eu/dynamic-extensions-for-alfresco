package com.github.dynamicextensionsalfresco.policy;

import org.alfresco.repo.policy.JavaBehaviour;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

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

        if (methodReference == null) {
            throw new IllegalArgumentException("methodReference is null");
        }
        this.methodReference = methodReference;
    }

    @Override
    @NotNull
    public String toString() {
        return methodReference.getDeclaringClass().getName() + "." + methodReference.getName();
    }
}
