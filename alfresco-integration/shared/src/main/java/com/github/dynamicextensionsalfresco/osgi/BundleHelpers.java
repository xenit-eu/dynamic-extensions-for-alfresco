package com.github.dynamicextensionsalfresco.osgi;

import org.osgi.framework.Bundle;

public class BundleHelpers {

    private BundleHelpers() {
    }

    public static String getBundleStateDescription(int bundleState) {
        switch (bundleState) {
            case Bundle.UNINSTALLED:
                return "uninstalled";
            case Bundle.INSTALLED:
                return "installed";
            case Bundle.RESOLVED:
                return "resolved";
            case Bundle.STARTING:
                return "starting";
            case Bundle.STOPPING:
                return "stopping";
            case Bundle.ACTIVE:
                return "active";
            default:
                return "unknown";
        }
    }
}
