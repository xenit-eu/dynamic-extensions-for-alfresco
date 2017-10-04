package com.github.dynamicextensionsalfresco.actions;

import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;

import static org.mockito.Mockito.mock;

/**
 * @author Laurent Van der Linden
 */
public class DummyDictionaryService extends DictionaryComponent {
	@Override
	public DataTypeDefinition getDataType(Class<?> javaClass) {
		return mock(DataTypeDefinition.class);
	}

	@Override
	public DataTypeDefinition getDataType(QName name) {
		return mock(DataTypeDefinition.class);
	}
}
