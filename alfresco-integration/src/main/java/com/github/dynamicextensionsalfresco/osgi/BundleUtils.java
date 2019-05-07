package com.github.dynamicextensionsalfresco.osgi;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import org.osgi.framework.Bundle;

public class BundleUtils {

    private BundleUtils() {
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

    public static Boolean isActive(Bundle bundle) { return bundle.getState() == Bundle.ACTIVE; }
    public static BundleManifest createManifest(Bundle bundle) {
        return BundleManifestFactory.createBundleManifest(bundle.getHeaders());
    }
}
