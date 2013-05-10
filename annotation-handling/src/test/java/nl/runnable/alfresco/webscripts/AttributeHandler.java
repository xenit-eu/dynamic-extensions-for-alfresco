package nl.runnable.alfresco.webscripts;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

@ManagedBean
@WebScript
public class AttributeHandler {

	@Uri("/handleAttributeByName")
	public void handleAttributeByName(@Attribute final String name,
			@Attribute("explicitlyNamed") final String explicitlyNamed) {
	}

	@Uri("/handleAttributeByType")
	public void handleAttributeByType(@Attribute final Person person) {
	}

	@Attribute
	protected String getName() {
		return "attribute1";
	}

	@Attribute("explicitlyNamed")
	protected String getSomeAttribute() {
		return "attribute2";
	}

	@Attribute
	protected Person getSomePerson() {
		return new Person();
	}
}
