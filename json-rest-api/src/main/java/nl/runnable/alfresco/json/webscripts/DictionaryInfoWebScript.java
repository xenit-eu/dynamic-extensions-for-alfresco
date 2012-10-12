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

package nl.runnable.alfresco.json.webscripts;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.json.DictionarySerializationService;
import nl.runnable.alfresco.json.SerializationSettings;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

/**
 * Handles Web Script requests for information from the dictionary.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript
public class DictionaryInfoWebScript extends AbstractJsonWebScript {

	/* Status codes */

	private static final int BAD_REQUEST = 400;

	private static final int NOT_FOUND = 404;

	/* Dependencies */

	@Inject
	private DictionaryService dictionaryService;

	@Inject
	private NamespacePrefixResolver namespacePrefixResolver;

	@Inject
	private DictionarySerializationService dictionarySerializationService;

	/* Operations */

	/**
	 * 
	 * Obtains information on all namespaces.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Uri("/json-api/dictionary/namespaces")
	public void getNamespaces(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final SerializationSettings settings = createSerializationSettings(request);
		final JsonGenerator generator = createJsonGenerator(response, settings);
		getDictionarySerializationService().serializeNamespaces(settings, generator);
		generator.close();
	}

	/**
	 * Obtains information on all data types.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Uri("/json-api/dictionary/data-types")
	public void getDataTypes(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final SerializationSettings settings = createSerializationSettings(request);
		final JsonGenerator generator = createJsonGenerator(response, settings);
		getDictionarySerializationService().serializeDataTypeDefinitions(settings, generator);
		generator.close();
	}

	/**
	 * Obtains an index of all available models.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Uri("/json-api/dictionary/models")
	public void getModelIndex(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final SerializationSettings settings = createSerializationSettings(request);
		final JsonGenerator generator = createJsonGenerator(response, settings);
		getDictionarySerializationService().serializeModelIndex(settings, generator);
		generator.close();
	}

	/**
	 * Obtains detail information on a given model.
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Uri("/json-api/dictionary/models/{model}")
	public void getModel(@UriVariable("model") final String model, final WebScriptRequest request,
			final WebScriptResponse response) throws IOException {
		final ModelDefinition modelDefinition = getModelDefinition(model, response);
		if (modelDefinition != null) {
			final SerializationSettings settings = createSerializationSettings(request);
			final JsonGenerator generator = createJsonGenerator(response, settings);
			getDictionarySerializationService().serializeModelDefinition(modelDefinition, settings, generator);
			generator.close();
		}
	}

	/**
	 * Obtains metadata information on a given model.
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Uri("/json-api/dictionary/models/{model}/metadata")
	public void getModelMetadata(@UriVariable("model") final String model, final WebScriptRequest request,
			final WebScriptResponse response) throws IOException {
		final ModelDefinition modelDefinition = getModelDefinition(model, response);
		if (modelDefinition != null) {
			final SerializationSettings settings = createSerializationSettings(request);
			final JsonGenerator generator = createJsonGenerator(response, settings);
			getDictionarySerializationService().serializeModelMetadata(modelDefinition, settings, generator);
			generator.close();
		}
	}

	/**
	 * Obtains detail information on a given Type or Aspect.
	 * 
	 * @param name
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Uri("/json-api/dictionary/classes/{class}")
	public void getType(@UriVariable("class") final String name, final WebScriptRequest request,
			final WebScriptResponse response) throws IOException {
		final QName qName = parseQName(name);
		if (qName != null) {
			final ClassDefinition classDefinition = getDictionaryService().getClass(qName);
			if (classDefinition != null) {
				final SerializationSettings settings = createSerializationSettings(request);
				final JsonGenerator generator = createJsonGenerator(response, settings);
				getDictionarySerializationService().serializeClassDefinition(classDefinition, settings, generator);
				generator.close();
			} else {
				writeJsonMessage(NOT_FOUND, String.format("Could not find type or aspect: %s", name), response);
			}
		} else {
			writeJsonMessage(BAD_REQUEST, String.format("Invalid class name: %s", name), response);
		}
	}

	/* Utility operations */

	protected QName parseQName(final String name) {
		Assert.hasText(name, "Name cannot be empty.");

		// TODO: Fine-tune the QName regular expressions.
		QName qName = null;
		if (name.matches(".+?:.+?")) {
			qName = QName.createQName(name, getNamespacePrefixResolver());
		} else if (name.matches("\\{.+?\\}.+?")) {
			qName = QName.createQName(name);
		}
		return qName;
	}

	protected ModelDefinition getModelDefinition(final String model, final WebScriptResponse response)
			throws IOException {
		ModelDefinition modelDefinition = null;
		final QName modelName = parseQName(model);
		if (modelName != null) {
			modelDefinition = getDictionaryService().getModel(modelName);
			if (modelDefinition == null) {
				writeJsonMessage(NOT_FOUND, String.format("Could not find model: %s", model), response);
			} else {
			}
		} else {
			writeJsonMessage(BAD_REQUEST, String.format("Invalid model name: %s", model), response);
		}
		return modelDefinition;
	}

	/* Dependencies */

	public void setDictionaryService(final DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	protected DictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	protected NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}

	public void setDictionarySerializationService(final DictionarySerializationService dictionarySerializationService) {
		this.dictionarySerializationService = dictionarySerializationService;
	}

	protected DictionarySerializationService getDictionarySerializationService() {
		return dictionarySerializationService;
	}
}
