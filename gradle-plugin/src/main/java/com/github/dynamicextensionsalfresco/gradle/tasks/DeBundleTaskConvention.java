package com.github.dynamicextensionsalfresco.gradle.tasks;

import aQute.bnd.gradle.BundleTaskConvention;
import aQute.bnd.osgi.Constants;
import com.github.dynamicextensionsalfresco.gradle.internal.BndHandler;
import org.gradle.api.tasks.bundling.Jar;

public class DeBundleTaskConvention {
    private Jar task;
    private BundleTaskConvention bndConvention;

    /**
     * Create a BundleTaskConvention for the specified Jar task.
     *
     * <p>
     * This also sets the default values for the added properties
     * and adds the bnd file to the task inputs.
     *
     * @param task
     */
    public DeBundleTaskConvention(Jar task) {
        this.task = task;
        bndConvention = new BundleTaskConvention(task);
        task.getConvention().getPlugins().put("_bundle_bnd", bndConvention);
    }

    public void buildDeBundle() {
        BndHandler bndHandler = new BndHandler(task, bndConvention);

        bndHandler.setHeader("Alfresco-Dynamic-Extension", "true");
        if(!bndHandler.hasHeader(Constants.IMPORT_PACKAGE)) {
            bndHandler.setHeader(Constants.IMPORT_PACKAGE, "*");
            bndHandler.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*");
        }
        bndConvention.buildBundle();
    }

}
