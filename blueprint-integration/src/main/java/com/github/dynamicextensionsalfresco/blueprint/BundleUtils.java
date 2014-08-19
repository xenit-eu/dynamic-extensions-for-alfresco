package com.github.dynamicextensionsalfresco.blueprint;

import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Utility class taken from Blueprint implementation. The Java package in which the original BundleUtils resides is not
 * exported.
 * 
 * @author Laurens Fridael
 * 
 */
@SuppressWarnings("deprecation")
class BundleUtils {

	public static final String DM_CORE_ID = "spring.osgi.core.bundle.id";
	public static final String DM_CORE_TS = "spring.osgi.core.bundle.timestamp";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Bundle getDMCoreBundle(final BundleContext ctx) {
		final ServiceReference ref = ctx.getServiceReference(PackageAdmin.class.getName());
		if (ref != null) {
			final Object service = ctx.getService(ref);
			if (service instanceof PackageAdmin) {
				final PackageAdmin pa = (PackageAdmin) service;
                return pa.getBundle(OsgiBundleXmlApplicationContext.class);
            }
		}
		return null;
	}

	public static String createNamespaceFilter(final BundleContext ctx) {
		final Bundle bnd = getDMCoreBundle(ctx);
		if (bnd != null) {
			return "(|(" + DM_CORE_ID + "=" + bnd.getBundleId() + ")(" + DM_CORE_TS + "=" + bnd.getLastModified()
					+ "))";
		}
		return "";
	}
}
