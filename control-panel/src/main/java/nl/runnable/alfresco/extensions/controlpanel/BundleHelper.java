package nl.runnable.alfresco.extensions.controlpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ManagedBean;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@ManagedBean
public class BundleHelper implements BundleContextAware {

	private static final String ALFRESCO_DYNAMIC_EXTENSION_HEADER = "Alfresco-Dynamic-Extension";

	/* Dependencies */

	private BundleContext bundleContext;

	/* Main operations */

	public List<TemplateBundle> getFrameworkBundles() {
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (Boolean.valueOf(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER)) == false) {
				templateBundles.add(new TemplateBundle(bundle));
			}
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	public List<TemplateBundle> getExtensionBundles() {
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (Boolean.valueOf(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER))) {
				templateBundles.add(new TemplateBundle(bundle));
			}
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	/* Dependencies */

	@Override
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;

	}
}
