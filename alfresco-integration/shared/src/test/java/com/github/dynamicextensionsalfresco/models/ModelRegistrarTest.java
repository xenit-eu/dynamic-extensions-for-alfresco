package com.github.dynamicextensionsalfresco.models;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * {@link ModelRegistrar} unit test.
 * 
 * @author Laurent Van der Linden
 * 
 */
public class ModelRegistrarTest {

	private DAOModelRegistrar daoModelRegistrar;

	private DictionaryDAO dictionaryDAOMock;

	private List<M2ModelResource> models;

	@Before
	public void setup() {
		dictionaryDAOMock = mock(DictionaryDAO.class);

		daoModelRegistrar = new DAOModelRegistrar(dictionaryDAOMock, new M2ModelListProvider() {
			@NotNull
			@Override
			public List<M2ModelResource> getModels() {
				return models;
			}
		});
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
		models = asList(
				new M2ModelResource(null, user),
				new M2ModelResource(null, provider),
				new M2ModelResource(null, superProvider)
		);

		user.createImport("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createNamespace("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createImport("http://www.alfresco.org/model/superprovider/1.0", "superprovider");

		superProvider.createNamespace("http://www.alfresco.org/model/superprovider/1.0", "superprovider");

		final ArgumentCaptor<M2Model> modelParameter = ArgumentCaptor.forClass(M2Model.class);

		daoModelRegistrar.registerModels();

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
		models = asList(
				new M2ModelResource(null, user),
				new M2ModelResource(null, provider)
		);

		// create bidirectional dependency between provider and user
		user.createNamespace("http://www.alfresco.org/model/user/1.0", "provider");
		user.createImport("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createNamespace("http://www.alfresco.org/model/provider/1.0", "provider");
		provider.createImport("http://www.alfresco.org/model/user/1.0", "user");

		daoModelRegistrar.registerModels();
	}
}
