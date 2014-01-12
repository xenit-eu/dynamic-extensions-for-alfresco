package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;

import org.springframework.stereotype.Component;

@Component
@Spied
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
