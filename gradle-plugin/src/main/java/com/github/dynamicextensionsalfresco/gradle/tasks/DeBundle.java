package com.github.dynamicextensionsalfresco.gradle.tasks;

import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;

public class DeBundle extends Jar {
    private DeBundleTaskConvention bundleTaskConvention;
    public DeBundle() {
        bundleTaskConvention = new DeBundleTaskConvention(this);
        getConvention().getPlugins().put("bundle", bundleTaskConvention);
    }

    @TaskAction
    @Override
    protected void copy() {
        bundleTaskConvention.buildDeBundle();
        super.copy();
    }
}
