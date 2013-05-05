package nl.runnable.alfresco.webscripts;

import java.util.Map;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Before;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

@ManagedBean
@WebScript
public class BeforeHandler {

	@Uri("/handleBefore")
	public void handleBefore(@Attribute final String name) {
	}

	@Before
	protected void populateModel(final Map<String, Object> model) {
		model.put("name", "attribute");
	}
}
