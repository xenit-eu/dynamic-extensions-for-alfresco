package nl.runnable.alfresco.actions;

import org.alfresco.repo.dictionary.DictionaryComponent;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;

import static org.mockito.Mockito.mock;

/**
 * @author Laurent Van der Linden
 */
public class DummyDictionaryService extends DictionaryComponent {
	@Override
	public DataTypeDefinition getDataType(Class<?> javaClass) {
		return mock(DataTypeDefinition.class);
	}
}
