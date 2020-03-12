package com.github.dynamicextensionsalfresco.blueprint;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import java.util.List;
import org.junit.Test;

public class DynamicExtensionsApplicationContextSharedTest {
    public static class UtilityOperationsTest {
        @Test
        public void recursifyPackage() {
            List<String> packagesRecursive = DynamicExtensionsApplicationContextBase.recursifyPackage("eu.xenit.test");
            assertThat(packagesRecursive, containsInAnyOrder("eu", "eu.xenit", "eu.xenit.test"));
            assertThat(packagesRecursive, hasSize(3));
        }
    }
}