package nl.runnable.alfresco.models;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import nl.runnable.alfresco.models.ModelRegistrar;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * {@link ModelRegistrar} unit test.
 * 
 * @author Laurent Van der Linden
 * 
 */
public class ModelRegistrarTest {

	private ModelRegistrar modelRegistrar;

	private DictionaryDAO dictionaryDAOMock;

	@Before
	public void setup() {
		modelRegistrar = new ModelRegistrar();
		dictionaryDAOMock = mock(DictionaryDAO.class);

		modelRegistrar.setDictionaryDao(dictionaryDAOMock);
	}

	/**
	 * Tests {@link ModelRegistrar#registerModels()}.
	 */
	@Test
	public void testRegisterDependantModels() {
		// test regular dependant models
		final M2Model user = M2Model.createModel("user");
		final M2Model provider = M2Model.createModel("provider");
		final M2Model superProvider = M2Model.createModel("superprovider");
		modelRegistrar.setModels(asList(user, provider, superProvider));

		user.createImport("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createNamespace("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createImport("http://www.alfresco.org/model/superprovider/1.0", "superprovider");

		superProvider.createNamespace("http://www.alfresco.org/model/superprovider/1.0", "superprovider");

		final ArgumentCaptor<M2Model> modelParameter = ArgumentCaptor.forClass(M2Model.class);

		modelRegistrar.registerModels();

		verify(dictionaryDAOMock, times(3)).putModel(modelParameter.capture());
		final List<M2Model> allValues = modelParameter.getAllValues();

		assertEquals(superProvider, allValues.get(0));
		assertEquals(provider, allValues.get(1));
		assertEquals(user, allValues.get(2));
	}

	/**
	 * Tests {@link ModelRegistrar#registerModels()}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCircularReferenceModels() {
		final M2Model user = M2Model.createModel("user");
		final M2Model provider = M2Model.createModel("provider");
		modelRegistrar.setModels(asList(user, provider));

		// create bidirectional dependency between provider and user
		user.createNamespace("http://www.alfresco.org/model/user/1.0", "provider");
		user.createImport("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createNamespace("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createImport("http://www.alfresco.org/model/user/1.0", "user");

		modelRegistrar.registerModels();
	}
}
