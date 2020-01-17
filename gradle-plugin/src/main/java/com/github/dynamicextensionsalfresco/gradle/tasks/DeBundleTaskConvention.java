package com.github.dynamicextensionsalfresco.gradle.tasks;

import aQute.bnd.gradle.BundleTaskConvention;
import aQute.bnd.osgi.Constants;
import com.github.dynamicextensionsalfresco.gradle.internal.BndHandler;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.Jar;

/**
 * The Dynamic Extensions Bundle TaskConvention applies the bnd {@link BundleTaskConvention} and updates OSGi headers so it is a valid Dynamic Extension
 *
 * The TaskConvention does not contribute any additional user-modifiable properties or methods to the task it is applied to.
 */
public class DeBundleTaskConvention {
    private final Jar task;
    private final BundleTaskConvention bndConvention;

    /**
     * Create a DeBundleTaskConvention for the specified Jar task.
     *
     * @param task The {@link Jar} task the convention will be applied to
     */
    public DeBundleTaskConvention(Jar task) {
        this.task = task;
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

    /**
     * Internal function that is called in a {@link org.gradle.api.Task#doLast(Action)} action to update OSGi headers and then run the BND bundle build
     */
    private void buildDeBundle() {
        BndHandler bndHandler = new BndHandler(task, bndConvention);

        bndHandler.setHeader("Alfresco-Dynamic-Extension", "true");
        if(!bndHandler.hasHeader(Constants.IMPORT_PACKAGE)) {
            bndHandler.setHeader(Constants.IMPORT_PACKAGE, "*");
            bndHandler.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*");
        }
        bndConvention.buildBundle();
    }

}
