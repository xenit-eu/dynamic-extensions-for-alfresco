/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.repository.dictionary;

import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.dictionary.M2Model;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
