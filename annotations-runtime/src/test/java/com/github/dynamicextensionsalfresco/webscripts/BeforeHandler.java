package com.github.dynamicextensionsalfresco.webscripts;

import java.util.Map;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Before;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;

import org.springframework.stereotype.Component;

@Component
@Spied
public class BeforeHandler {

	@Before
	protected void populateModel(final Map<String, Object> model) {
		model.put("name", "attribute");
	}

	@Uri("/handleBefore")
	public void handleBefore(@Attribute final String name) {
	}

}
