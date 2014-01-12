package com.github.dynamicextensionsalfresco.osgi.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStringListFactoryBean extends AbstractTextConfigurationFileFactoryBean<List<String>> {

	/* State */

	private List<String> values;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends List<String>> getObjectType() {
		return (Class<? extends List<String>>) (Class<?>) List.class;
	}

	@Override
	public List<String> getObject() throws IOException {
		if (values == null) {
			values = createValues();
		}
		return values;
	}

	/* Utility operations */

	private List<String> createValues() throws IOException {
		final List<String> values = new ArrayList<String>();
		readConfigurations(new LineCallback() {

			@Override
			public void doWithLine(final String line) {
				values.add(line);
			}
		});
		return values;
	}

}
