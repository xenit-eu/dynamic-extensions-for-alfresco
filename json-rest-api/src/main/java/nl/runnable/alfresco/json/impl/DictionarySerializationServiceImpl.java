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

package nl.runnable.alfresco.json.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.json.DictionarySerializationService;
import nl.runnable.alfresco.json.SerializationSettings;

import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.NamespaceDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.QName;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.util.Assert;

/**
 * {@link JsonGenerator}-based {@link DictionarySerializationService} implementation.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
public class DictionarySerializationServiceImpl extends AbstractSerializationService implements
		DictionarySerializationService {

	/* Dependencies */

	@Inject
	private DictionaryService dictionaryService;

	/* Main operations */

	public void serializeNamespaces(final SerializationSettings settings, final JsonGenerator generator)
			throws IOException {
		generator.writeStartArray();
		for (final String uri : getNamespacePrefixResolver().getURIs()) {
			generator.writeStartObject();
			generator.writeStringField("uri", uri);
			generator.writeArrayFieldStart("prefixes");
			for (final String prefix : getNamespacePrefixResolver().getPrefixes(uri)) {
				generator.writeString(prefix);
			}
			generator.writeEndArray();
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}

	public void serializeModelIndex(final SerializationSettings settings, final JsonGenerator generator)
			throws IOException {
		generator.writeStartArray();
		final List<QName> modelNames = new ArrayList<QName>(getDictionaryService().getAllModels());
		Collections.sort(modelNames);
		for (final QName modelName : modelNames) {
			final ModelDefinition model = dictionaryService.getModel(modelName);
			generator.writeStartObject();
			serializeModelInfo(model, settings, generator);
			generator.writeStringField("location", location(settings, "/json-api/dictionary/models/", modelName));
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}

	public void serializeModelMetadata(final ModelDefinition modelDefinition, final SerializationSettings settings,
			final JsonGenerator generator) throws IOException {
		generator.writeStartObject();
		serializeModelInfo(modelDefinition, settings, generator);
		generator.writeArrayFieldStart("types");
		final List<QName> types = new ArrayList<QName>(getDictionaryService().getTypes(modelDefinition.getName()));
		Collections.sort(types);
		for (final QName type : types) {
			generator.writeStartObject();
			generator.writeStringField("model", formattedQName(modelDefinition.getName(), settings.getQNameFormat()));
			generator.writeStringField("name", formattedQName(type, settings.getQNameFormat()));
			generator.writeEndObject();
		}
		generator.writeEndArray();
		generator.writeArrayFieldStart("aspects");
		final List<QName> aspects = new ArrayList<QName>(getDictionaryService().getAspects(modelDefinition.getName()));
		Collections.sort(aspects);
		for (final QName aspect : aspects) {
			generator.writeStartObject();
			generator.writeStringField("model", formattedQName(modelDefinition.getName(), settings.getQNameFormat()));
			generator.writeStringField("name", formattedQName(aspect, settings.getQNameFormat()));
			generator.writeEndObject();
		}
		generator.writeEndArray();
		generator.writeEndObject();
	}

	public void serializeModelDefinition(final ModelDefinition modelDefinition, final SerializationSettings settings,
			final JsonGenerator generator) throws IOException {
		Assert.notNull(modelDefinition, "ModelDefinition cannot be null.");
		Assert.notNull(settings, "SerializationSettings cannot be null.");
		Assert.notNull(generator, "JsonGenerator cannot be null.");

		generator.writeStartObject();
		serializeModelInfo(modelDefinition, settings, generator);
		generator.writeArrayFieldStart("types");
		final List<QName> types = new ArrayList<QName>(getDictionaryService().getTypes(modelDefinition.getName()));
		Collections.sort(types);
		for (final QName type : types) {
			final TypeDefinition typeDefinition = getDictionaryService().getType(type);
			serializeClassDefinition(typeDefinition, settings, generator);
		}
		generator.writeEndArray();
		generator.writeArrayFieldStart("aspects");
		final List<QName> aspects = new ArrayList<QName>(getDictionaryService().getAspects(modelDefinition.getName()));
		Collections.sort(aspects);
		for (final QName aspect : aspects) {
			final AspectDefinition aspectDefinition = getDictionaryService().getAspect(aspect);
			serializeClassDefinition(aspectDefinition, settings, generator);
		}
		generator.writeEndArray();
		generator.writeEndObject();
	}

	public void serializeClassDefinition(final ClassDefinition classDefinition, final SerializationSettings settings,
			final JsonGenerator generator) throws IOException {
		Assert.notNull(classDefinition, "ClassDefinition cannot be null.");
		Assert.notNull(settings, "SerializationSettings cannot be null.");
		Assert.notNull(generator, "JsonGenerator cannot be null.");

		generator.writeStartObject();
		generator.writeStringField("model",
				formattedQName(classDefinition.getModel().getName(), settings.getQNameFormat()));
		generator.writeStringField("name", formattedQName(classDefinition.getName(), settings.getQNameFormat()));
		generator.writeStringField("title", classDefinition.getTitle());
		generator.writeStringField("description", classDefinition.getDescription());
		final QName parentName = classDefinition.getParentName();
		if (parentName != null) {
			final ClassDefinition parentDefinition = getDictionaryService().getClass(parentName);
			if (parentDefinition != null) {
				generator.writeFieldName("parent");
				serializeClassDefinitionReference(parentDefinition, settings, generator);
			} else {
				generator.writeNullField("parent");
			}
		} else {
			generator.writeNullField("parent");
		}
		generator.writeBooleanField("aspect", classDefinition.isAspect());
		final Boolean archive = classDefinition.getArchive();
		if (archive != null) {
			generator.writeBooleanField("archive", archive);
		} else {
			generator.writeNullField("archive");
		}

		generator.writeBooleanField("includedInSuperTypeQuery", classDefinition.getIncludedInSuperTypeQuery());
		generator.writeArrayFieldStart("properties");
		final List<PropertyDefinition> propertyDefinitions = new ArrayList<PropertyDefinition>(classDefinition
				.getProperties().values());
		Collections.sort(propertyDefinitions, new Comparator<PropertyDefinition>() {

			public int compare(final PropertyDefinition a, final PropertyDefinition b) {
				return a.getName().compareTo(b.getName());
			}
		});
		for (final PropertyDefinition propertyDefinition : propertyDefinitions) {
			serializePropertyDefinition(propertyDefinition, settings, generator);
		}
		generator.writeEndArray();
		generator.writeArrayFieldStart("associations");
		for (final AssociationDefinition associationDefinition : classDefinition.getAssociations().values()) {
			serializeAssociationDefinition(associationDefinition, settings, generator);
		}
		generator.writeEndArray();
		generator.writeBooleanField("container", classDefinition.isContainer());
		generator.writeArrayFieldStart("childAssociations");
		for (final ChildAssociationDefinition childAssociationDefinition : classDefinition.getChildAssociations()
				.values()) {
			serializeAssociationDefinition(childAssociationDefinition, settings, generator);
		}
		generator.writeEndArray();
		generator.writeArrayFieldStart("defaultAspects");
		for (final QName aspect : classDefinition.getDefaultAspectNames()) {
			serializeClassDefinitionReference(getDictionaryService().getAspect(aspect), settings, generator);
		}
		generator.writeEndArray();
		generator.writeEndObject();
	}

	public void serializePropertyDefinition(final PropertyDefinition propertyDefinition,
			final SerializationSettings settings, final JsonGenerator generator) throws IOException {
		Assert.notNull(propertyDefinition, "PropertyDefinition cannot be null.");
		Assert.notNull(settings, "SerializationSettings cannot be null.");
		Assert.notNull(generator, "JsonGenerator cannot be null.");

		generator.writeStartObject();
		generator.writeStringField("name", formattedQName(propertyDefinition.getName(), settings.getQNameFormat()));
		generator.writeFieldName("containerClass");
		serializeClassDefinitionReference(propertyDefinition.getContainerClass(), settings, generator);
		generator.writeStringField("title", propertyDefinition.getTitle());
		generator.writeStringField("description", propertyDefinition.getDescription());
		generator.writeStringField("dataType",
				formattedQName(propertyDefinition.getDataType().getName(), settings.getQNameFormat()));
		generator.writeStringField("defaultValue", propertyDefinition.getDefaultValue());
		generator.writeBooleanField("mandatory", propertyDefinition.isMandatory());
		generator.writeBooleanField("mandatoryEnforced", propertyDefinition.isMandatory());
		generator.writeBooleanField("multiValued", propertyDefinition.isMultiValued());
		generator.writeBooleanField("protected", propertyDefinition.isProtected());
		generator.writeBooleanField("indexed", propertyDefinition.isIndexed());
		generator.writeBooleanField("storedInIndex", propertyDefinition.isStoredInIndex());
		generator.writeEndObject();
	}

	public void serializeAssociationDefinition(final AssociationDefinition associationDefinition,
			final SerializationSettings settings, final JsonGenerator generator) throws IOException {
		generator.writeStartObject();
		generator.writeStringField("name", formattedQName(associationDefinition.getName(), settings.getQNameFormat()));
		generator.writeStringField("title", associationDefinition.getTitle());
		generator.writeStringField("description", associationDefinition.getDescription());
		generator.writeObjectFieldStart("source");
		generator.writeFieldName("class");
		serializeClassDefinitionReference(associationDefinition.getSourceClass(), settings, generator);
		generator.writeBooleanField("mandatory", associationDefinition.isSourceMandatory());
		generator.writeBooleanField("many", associationDefinition.isSourceMany());
		generator.writeStringField("role",
				formattedQName(associationDefinition.getSourceRoleName(), settings.getQNameFormat()));
		generator.writeEndObject(); // source
		generator.writeObjectFieldStart("target");
		generator.writeFieldName("class");
		serializeClassDefinitionReference(associationDefinition.getTargetClass(), settings, generator);
		generator.writeBooleanField("mandatory", associationDefinition.isTargetMandatory());
		generator.writeBooleanField("mandatoryEnforced", associationDefinition.isTargetMandatoryEnforced());
		generator.writeBooleanField("many", associationDefinition.isTargetMany());
		generator.writeStringField("role",
				formattedQName(associationDefinition.getTargetRoleName(), settings.getQNameFormat()));
		generator.writeEndObject(); // target
		generator.writeBooleanField("protected", associationDefinition.isProtected());
		if (associationDefinition instanceof ChildAssociationDefinition) {
			final ChildAssociationDefinition childAssociationDefinition = (ChildAssociationDefinition) associationDefinition;
			generator.writeBooleanField("duplicateChildNamesAllowed",
					childAssociationDefinition.getDuplicateChildNamesAllowed());
			generator.writeBooleanField("propagateTimestamps", childAssociationDefinition.getPropagateTimestamps());
			generator.writeStringField("requiredChildName", childAssociationDefinition.getRequiredChildName());
		}
		generator.writeEndObject();
	}

	public void serializeDataTypeDefinitions(final SerializationSettings settings, final JsonGenerator generator)
			throws IOException {
		generator.writeStartArray();
		for (final QName dataType : getDictionaryService().getAllDataTypes()) {
			final DataTypeDefinition dataTypeDefinition = getDictionaryService().getDataType(dataType);
			serializeDataTypeDefinition(dataTypeDefinition, settings, generator);
		}
		generator.writeEndArray();
	}

	public void serializeDataTypeDefinition(final DataTypeDefinition dataTypeDefinition,
			final SerializationSettings settings, final JsonGenerator generator) throws IOException {
		Assert.notNull(dataTypeDefinition, "DataTypeDefinition cannot be null.");
		Assert.notNull(settings, "SerializationSettings cannot be null.");
		Assert.notNull(generator, "JsonGenerator cannot be null.");

		generator.writeStartObject();
		generator.writeStringField("name", formattedQName(dataTypeDefinition.getName(), settings.getQNameFormat()));
		generator.writeStringField("title", dataTypeDefinition.getTitle());
		generator.writeStringField("description", dataTypeDefinition.getDescription());
		generator.writeStringField("model",
				formattedQName(dataTypeDefinition.getModel().getName(), settings.getQNameFormat()));
		generator.writeStringField("javaClassName", dataTypeDefinition.getJavaClassName());
		generator.writeStringField("analyserClassName", getAnalyserClassName(dataTypeDefinition));
		generator.writeEndObject();
	}

	/* Utility operations */

	protected void serializeModelInfo(final ModelDefinition modelDefinition, final SerializationSettings settings,
			final JsonGenerator generator) throws IOException, JsonGenerationException {
		generator.writeStringField("name", formattedQName(modelDefinition.getName(), settings.getQNameFormat()));
		generator.writeStringField("author", modelDefinition.getAuthor());
		generator.writeStringField("publishedDate", date(modelDefinition.getPublishedDate()));
		generator.writeStringField("description", modelDefinition.getDescription());
		generator.writeStringField("version", modelDefinition.getVersion());
		generator.writeArrayFieldStart("namespaces");
		for (final NamespaceDefinition namespace : modelDefinition.getNamespaces()) {
			generator.writeStartObject();
			generator.writeStringField("prefix", namespace.getPrefix());
			generator.writeStringField("uri", namespace.getUri());
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}

	protected void serializeClassDefinitionReference(final ClassDefinition classDefinition,
			final SerializationSettings settings, final JsonGenerator generator) throws IOException,
			JsonGenerationException {
		generator.writeStartObject();
		generator.writeStringField("name", formattedQName(classDefinition.getName(), settings.getQNameFormat()));
		generator.writeStringField("model",
				formattedQName(classDefinition.getModel().getName(), settings.getQNameFormat()));
		generator.writeEndObject();
	}

	/**
	 * Resolves the analyser class name for the given DataTypeDefinition.
	 * 
	 * @param dataTypeDefinition
	 */
	protected String getAnalyserClassName(final DataTypeDefinition dataTypeDefinition) {
		String className = null;
		final Method method = resolveAnalyserClassNameMethod(dataTypeDefinition);
		if (method != null) {
			try {
				className = (String) method.invoke(dataTypeDefinition);
			} catch (final Exception e) {
			}
		}
		return className;
	}

	/**
	 * Resolves the method for obtaining the analyser class name. Takes into account differences in the Alfresco 3.4 and
	 * 4.0 API.
	 * 
	 * @param dataTypeDefinition
	 * @return
	 */
	private Method resolveAnalyserClassNameMethod(final DataTypeDefinition dataTypeDefinition) {
		Method method = null;
		try {
			// Alfresco 3.4 API
			method = dataTypeDefinition.getClass().getMethod("getAnalyserClassName");
		} catch (final SecurityException e) {
		} catch (final NoSuchMethodException e) {
			try {
				// Alfresco 4.0 API
				method = dataTypeDefinition.getClass().getMethod("resolveAnalyserClassName");
			} catch (final SecurityException e1) {
			} catch (final NoSuchMethodException e1) {
			}
		}
		return method;
	}

	/* Dependencies */

	public void setDictionaryService(final DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	protected DictionaryService getDictionaryService() {
		return dictionaryService;
	}

}
