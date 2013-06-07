package nl.runnable.alfresco.policy.samples;

import org.alfresco.repo.policy.PropertyPolicy;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import java.io.Serializable;

/**
 * Alfresco has no {@link PropertyPolicy} subtypes, so create a test version.
 *
 * @author Laurent Van der Linden
 */
public interface DummyPropertyPolicy extends PropertyPolicy {
	public static final QName QNAME = QName.createQName(NamespaceService.ALFRESCO_URI, "onNewValue");

	void onNewValue(Serializable newValue);
}
