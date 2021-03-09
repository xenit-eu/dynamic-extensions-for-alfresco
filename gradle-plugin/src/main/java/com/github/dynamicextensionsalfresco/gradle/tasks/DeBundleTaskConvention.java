package com.github.dynamicextensionsalfresco.gradle.tasks;

import aQute.bnd.gradle.BundleTaskConvention;
import aQute.bnd.osgi.Constants;
import java.util.Arrays;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Jar;

/**
 * The Dynamic Extensions Bundle TaskConvention applies the bnd {@link BundleTaskConvention} and updates OSGi headers so it is a valid Dynamic Extension
 *
 * The TaskConvention does not contribute any additional user-modifiable properties or methods to the task it is applied to.
 */
public class DeBundleTaskConvention {
    private final BundleTaskConvention bndConvention;

    /**
     * Create a DeBundleTaskConvention for the specified Jar task.
     *
     * @param task The {@link Jar} task the convention will be applied to
     */
    public DeBundleTaskConvention(Jar task) {
        bndConvention = new BundleTaskConvention(task);
        task.getConvention().getPlugins().put("_bundle_bnd", bndConvention);
        // This is an anonymous inner class instead of a lambda because of a gradle issue where lambdas break up-to-date checks
        task.doLast("buildDeBundle", new Action<Task>() {
            @Override
            public void execute(Task task) {
                buildDeBundle();
            }
        });
    }

    private void buildDeBundle() {
        bndConvention.bnd("Alfresco-Dynamic-Extension=true");

        boolean hasImportPackage = Arrays.stream(bndConvention.getBnd().get().split("\n")).anyMatch(s -> s.startsWith(Constants.IMPORT_PACKAGE+"="));
        if(!hasImportPackage) {
            bndConvention.bnd(
                    Constants.IMPORT_PACKAGE+"=*",
                    Constants.DYNAMICIMPORT_PACKAGE+"=*"
            );
        }
        bndConvention.buildBundle();
    }

    public BundleTaskConvention getBndConvention() {
        return bndConvention;
    }

    public static void apply(TaskProvider<Jar> jarTaskProvider) {
        jarTaskProvider.configure(DeBundleTaskConvention::apply);
    }

    public static DeBundleTaskConvention apply(Jar jar) {
        DeBundleTaskConvention convention = jar.getConvention().findPlugin(DeBundleTaskConvention.class);
        if(convention == null) {
            convention = new DeBundleTaskConvention(jar);
            jar.getConvention().getPlugins().put("bundle", convention);
        }
        return convention;
    }

    public static DeBundleTaskConvention get(Jar jar) {
        return jar.getConvention().getPlugin(DeBundleTaskConvention.class);
    }
}
