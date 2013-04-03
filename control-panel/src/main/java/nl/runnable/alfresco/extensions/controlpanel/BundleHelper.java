package nl.runnable.alfresco.extensions.controlpanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.extensions.controlpanel.template.TemplateBundle;

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
			if (isDynamicExtension(bundle) == false) {
				templateBundles.add(new TemplateBundle(bundle));
			}
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	public List<TemplateBundle> getExtensionBundles() {
		final List<TemplateBundle> templateBundles = new ArrayList<TemplateBundle>();
		for (final Bundle bundle : bundleContext.getBundles()) {
			if (isDynamicExtension(bundle)) {
				templateBundles.add(new TemplateBundle(bundle));
			}
		}
		Collections.sort(templateBundles);
		return templateBundles;
	}

	/* Utility operations */

	/**
	 * Tests if the given bundle contains a Dynamic Extension.
	 * <p>
	 * This implementation looks if the bundle header <code>Alfresco-Dynamic-Extension</code> equals the String "true".
	 * 
	 * @param bundle
	 * @return
	 */
	private boolean isDynamicExtension(final Bundle bundle) {
		return "true".equals(bundle.getHeaders().get(ALFRESCO_DYNAMIC_EXTENSION_HEADER));
	}

	/* Dependencies */

	@Override
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;

	}
}
