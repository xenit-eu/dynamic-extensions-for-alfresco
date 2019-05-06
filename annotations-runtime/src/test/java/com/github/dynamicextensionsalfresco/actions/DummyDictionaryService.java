package com.github.dynamicextensionsalfresco.actions;

import static org.mockito.Mockito.*;

import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.namespace.QName;

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
