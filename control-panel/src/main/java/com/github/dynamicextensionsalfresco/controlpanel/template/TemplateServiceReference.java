package nl.runnable.alfresco.controlpanel.template;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.util.Assert;

/**
 * Adapts a {@link ServiceReference} for display in a Freemarker template.
 * 
 * @author Laurens Fridael
 * 
 */
public class TemplateServiceReference implements Comparable<TemplateServiceReference> {

	private static final String SERVICE_TYPE_PROPERTY = "alfresco.service.type";

	private static final String BEAN_NAME_PROPERTY = "osgi.service.blueprint.compname";

	@SuppressWarnings("rawtypes")
	private final ServiceReference serviceReference;

	@SuppressWarnings("rawtypes")
	public TemplateServiceReference(final ServiceReference serviceReference) {
		Assert.notNull(serviceReference);
		this.serviceReference = serviceReference;
	}

	public TemplateBundle getBundle() {
		return new TemplateBundle(serviceReference.getBundle());
	}

	public String[] getObjectClasses() {
		return (String[]) serviceReference.getProperty(Constants.OBJECTCLASS);
	}

	public String getBeanName() {
		return (String) serviceReference.getProperty(BEAN_NAME_PROPERTY);
	}

	public String getType() {
		return (String) serviceReference.getProperty(SERVICE_TYPE_PROPERTY);
	}

	public List<String> getPropertyKeys() {
		return Arrays.asList(serviceReference.getPropertyKeys());
	}

	public TemplateServiceReferenceProperties getProperties() {
		return new TemplateServiceReferenceProperties(serviceReference);
	}

	@Override
	public int compareTo(final TemplateServiceReference other) {
		int compare = 0;
		final String name1 = getObjectClasses()[0];
		final String name2 = other.getObjectClasses()[0];
		compare = name1.compareTo(name2);
		if (compare == 0) {
			String type1 = getType();
			if (type1 == null) {
				type1 = "";
			}
			String type2 = other.getType();
			if (type2 == null) {
				type2 = "";
			}
			compare = type1.compareTo(type2);
		}
		return compare;
	}
}
