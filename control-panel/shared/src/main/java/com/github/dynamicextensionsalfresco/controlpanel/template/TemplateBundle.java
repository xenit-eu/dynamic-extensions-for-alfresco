package com.github.dynamicextensionsalfresco.controlpanel.template;

import static java.util.Collections.emptyList;

import com.github.dynamicextensionsalfresco.osgi.BundleHelpers;
import com.github.dynamicextensionsalfresco.controlpanel.BundleHelper.BundleHelperCompanion;
import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import com.springsource.util.osgi.manifest.ExportedPackage;
import com.springsource.util.osgi.manifest.ImportedPackage;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import java.text.SimpleDateFormat;
import java.util.*;
import org.osgi.framework.Version;

/**
 * Adapts an [Bundle] for display in a Freemarker template.

 * @author Laurens Fridael
 */
public class TemplateBundle implements Comparable<TemplateBundle> {
    private int FRAMEWORK_BUNDLE_ID = 0;

    public TemplateBundle(Bundle bundle) {
        this(bundle, null);
    }
    public TemplateBundle(Bundle bundle, List<ServiceReference<Object>> services) {
        this.bundle = bundle;
        init(services);
    }

    private Bundle bundle;
    private List<TemplateServiceReference> services = null;

    public List<TemplateServiceReference> getServices() {
        return this.services;
    }

    private void init(List<ServiceReference<Object>> initServices){
        if(initServices == null) {
            this.services = emptyList();
            return;
        }
        this.services = Arrays.asList(
                            initServices.stream()
                                    .map(TemplateServiceReference::new)
                                    .sorted()
                                    .toArray(TemplateServiceReference[]::new)
                        );
    }

    public Long getBundleId(){
        return bundle != null ? bundle.getBundleId() : 0;
    }

    public String getSymbolicName() {
        return bundle != null && bundle.getSymbolicName() != null
                ? bundle.getSymbolicName()
                : "non OSGi jar file";
    }

    public String getName() {
        return getHeader(Constants.BUNDLE_NAME) != null
                ? getHeader(Constants.BUNDLE_NAME)
                : getSymbolicName();
    }

    public String getDescription() {
        return getHeader(Constants.BUNDLE_DESCRIPTION);
    }

    private String getHeader(String header) {
        return bundle != null && bundle.getHeaders() != null
                ? bundle.getHeaders().get(header)
                : null;
    }

    public Boolean getDynamicExtension() { return BundleHelperCompanion.isDynamicExtension(bundle);}

    public Boolean getFragmentBundle() { return getHeader(Constants.FRAGMENT_HOST) != null; }

    public String getLocation() { return bundle != null ? bundle.getLocation() : ""; }

    public String getLastModified() {
        if(bundle == null) {
            return null;
        }

        long lastModified = bundle.getLastModified();
        if (lastModified > 0) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(new Date(lastModified));
        } else {
            return null;
        }
    }

    public String getVersion() { return bundle != null ? bundle.getVersion().toString() : ""; }

    public String getStore() {
        String defaultStore = "n/a";
        if(bundle == null){
            return defaultStore;
        }

        if (bundle.getLocation().startsWith("file:")) {
            return "filesystem";
        } else if (bundle.getLocation().startsWith("/")) {
            return "repository";
        } else {
            return defaultStore;
        }
    }

    public String getStatus() {
        if(bundle == null){
            return BundleHelpers.getBundleStateDescription(0);
        }
        return BundleHelpers.getBundleStateDescription(bundle.getState());
    }

    public String getExportPackage() { return getHeader(Constants.EXPORT_PACKAGE); }

    public String getDocumentationUrl() { return getHeader(Constants.BUNDLE_DOCURL); }

    public Boolean getDeleteable() { return this.getLocation().startsWith("/Company Home"); }

    public List<TemplateImportedPackage> getImportedPackages() {
        ArrayList<TemplateImportedPackage> packages = new ArrayList<TemplateImportedPackage>();
        BundleManifest manifest = getManifest();
        if(manifest == null) {
            return packages;
        }

        for (ImportedPackage importedPackage : manifest.getImportPackage().getImportedPackages()) {
            TemplateImportedPackage bundlePackage = new TemplateImportedPackage();
            bundlePackage.setName(importedPackage.getPackageName());
            Version ceiling = importedPackage.getVersion().getCeiling();
            if (ceiling != null) {
                bundlePackage.setMaxVersion(ceiling.toString());
            }
            Version floor = importedPackage.getVersion().getFloor();
            if (floor != null) {
                bundlePackage.setMinVersion(floor.toString());
            }
            packages.add(bundlePackage);
        }
        return packages;
    }

    public List<ExportedPackage> getExportedPackages() {
        return getManifest() != null
                ? getManifest().getExportPackage().getExportedPackages()
                : new ArrayList<ExportedPackage>();
    }

    private BundleManifest manifestCache = null;
    public BundleManifest getManifest() {
        if(bundle == null){
            return null;
        }

        if(manifestCache == null) {
            manifestCache = BundleManifestFactory.createBundleManifest(bundle.getHeaders());
        }

        return manifestCache;
    }


    @Override
    public int compareTo(TemplateBundle other) {
        if(other == null) {
            return -1;
        }

        if (this.getBundleId().equals((long) FRAMEWORK_BUNDLE_ID)) {
            return Integer.MIN_VALUE;
        } else if (other.getBundleId().equals((long) FRAMEWORK_BUNDLE_ID)) {
            return Integer.MAX_VALUE;
        }

        int compare = this.getName().compareToIgnoreCase(other.getName());
        if (compare == 0) {
            return other.getVersion().compareTo(this.getVersion());
        }

        return compare;
    }
}
