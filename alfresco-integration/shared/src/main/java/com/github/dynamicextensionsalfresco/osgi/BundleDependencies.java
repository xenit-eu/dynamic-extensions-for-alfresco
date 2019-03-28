package com.github.dynamicextensionsalfresco.osgi;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import com.springsource.util.osgi.manifest.ExportedPackage;
import com.springsource.util.osgi.manifest.ImportedPackage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Bundle;

/**
 * Sort a list of bundles by their dependency graph. Bundles without or satisfied dependencies come first.
 *
 * @author Laurent Van der Linden.
 * @author Toon Geens
 */
public final class BundleDependencies {

    private static final BundleMetadataProvider bundleMetadataProvider = new BundleMetadataProvider();

    private BundleDependencies() {

    }

    public static List<Bundle> sortByDependencies(List<Bundle> bundles) {
        if (bundles.size() == 1) {
            return bundles;
        }

        List<BundleDescriptor> descriptors = Mapper.map(bundles, new MappingFunc<Bundle, BundleDescriptor>() {
            @Override
            public BundleDescriptor map(Bundle item) {
                return new BundleDescriptor(item);
            }
        });

        Collection<BundleDescriptor> sorted = DependencySorter.sort(descriptors, bundleMetadataProvider);

        return Mapper.map(sorted, new MappingFunc<BundleDescriptor, Bundle>() {
            @Override
            public Bundle map(BundleDescriptor item) {
                return item.getBundle();
            }
        });
    }

    public static class BundleMetadataProvider implements
            DependencyMetadataProvider<BundleDependencies.BundleDescriptor> {

        @Override
        public boolean allowCircularReferences() {
            // SLF4J has a circular reference
            return true;
        }

        @Override
        public Collection<Object> imports(BundleDependencies.BundleDescriptor item) {
            List<ImportedPackage> imports = item.getManifest().getImportPackage().getImportedPackages();
            return Mapper.map(imports, new MappingFunc<ImportedPackage, Object>() {
                @Override
                public Object map(ImportedPackage item) {
                    return item.getPackageName();
                }
            });
        }

        @Override
        public Collection<Object> exports(BundleDependencies.BundleDescriptor item) {
            List<ExportedPackage> exports = item.getManifest().getExportPackage().getExportedPackages();
            return Mapper.map(exports, new MappingFunc<ExportedPackage, Object>() {
                @Override
                public Object map(ExportedPackage item) {
                    return item.getPackageName();
                }
            });
        }
    }


    public static class BundleDescriptor {

        private final Bundle bundle;
        private final BundleManifest manifest;

        BundleDescriptor(@NotNull Bundle bundle) {
            Intrinsics.checkParameterIsNotNull(bundle, "bundle");

            this.bundle = bundle;
            this.manifest = BundleManifestFactory.createBundleManifest(bundle.getHeaders());
        }

        @Override
        public String toString() {
            return String.format("%3d: %s", bundle.getBundleId(), bundle.getSymbolicName());
        }

        BundleManifest getManifest() {
            return manifest;
        }

        Bundle getBundle() {
            return bundle;
        }
    }

    /*
     * Functional interface to support list mapping on Java 7
     */
    interface MappingFunc<T, R> {

        R map(T item);
    }

    static class Mapper {

        public static <T, R> List<R> map(Collection<T> list, MappingFunc<T, R> func) {
            Intrinsics.checkParameterIsNotNull(list, "list");
            Intrinsics.checkParameterIsNotNull(func, "func");

            List<R> result = new ArrayList<>(list.size());
            for (T item : list) {
                result.add(func.map(item));
            }
            return result;
        }
    }
}
