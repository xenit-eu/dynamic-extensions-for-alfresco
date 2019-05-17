package com.github.dynamicextensionsalfresco.gradle.tasks;

import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;

/**
 * Create a Dynamic Extensions bundle jar
 */
public class DeBundle extends Jar {
    private DeBundleTaskConvention bundleTaskConvention;

    /**
     * Create a Bundle task. This also adds the {@link DeBundleTaskConvention} convention
     */
    public DeBundle() {
        bundleTaskConvention = new DeBundleTaskConvention(this);
        getConvention().getPlugins().put("bundle", bundleTaskConvention);
    }
}
