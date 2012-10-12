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

package nl.runnable.alfresco.json;

import java.io.IOException;

import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.codehaus.jackson.JsonGenerator;

/**
 * Defines operations for serializing dictionary information.
 * 
 * @author Laurens Fridael
 * 
 */
public interface DictionarySerializationService {

	/**
	 * Serializes information on all namespaces. Implementation should provide the prefix and full namespace URI.
	 * 
	 * @param format
	 * @param generator
	 */
	public void serializeNamespaces(SerializationSettings format, JsonGenerator generator) throws IOException;

	/**
	 * Serializes an index of all available models. Implementation should provide summary information only.
	 * <p>
	 * To obtain detail information on a given model use
	 * {@link #serializeModelDefinition(ModelDefinition, SerializationSettings, JsonGenerator)}.
	 * 
	 * @param format
	 * @param generator
	 */
	public void serializeModelIndex(SerializationSettings format, JsonGenerator generator) throws IOException;

	/**
	 * Serialize a {@link ModelDefinition}'s metadata. Implementations should provide model metadata and a listing of
	 * types and aspects.
	 * <p>
	 * To obtain detail information on a given model use
	 * {@link #serializeModelDefinition(ModelDefinition, SerializationSettings, JsonGenerator)}.
	 * 
	 * @param modelDefinition
	 * @param format
	 * @param generator
	 * @throws IOException
	 */
	public void serializeModelMetadata(ModelDefinition modelDefinition, SerializationSettings format,
			JsonGenerator generator) throws IOException;

	/**
	 * Serializes a {@link ModelDefinition}. Implementations should provide full information on the model's types,
	 * aspects and properties.
	 * 
	 * @param modelDefinition
	 * @param serializationSettings
	 * @param generator
	 * @throws IOException
	 */
	public void serializeModelDefinition(ModelDefinition modelDefinition, SerializationSettings format,
			JsonGenerator generator) throws IOException;

	/**
	 * Serializes a {@link ClassDefinition}.
	 * 
	 * @param classDefinition
	 * @param format
	 * @param generator
	 * @throws IOException
	 */
	public void serializeClassDefinition(final ClassDefinition classDefinition, final SerializationSettings format,
			final JsonGenerator generator) throws IOException;

	/**
	 * Serializes a {@link DataTypeDefinition}.
	 * 
	 * @param dataTypeDefinition
	 * @param format
	 * @param generator
	 * @throws IOException
	 */
	public void serializePropertyDefinition(final PropertyDefinition propertyDefinition,
			final SerializationSettings format, final JsonGenerator generator) throws IOException;

	/**
	 * Serializes an {@link AssociationDefinition}.
	 * 
	 * @param associationDefinition
	 * @param format
	 * @param generator
	 * @throws IOException
	 */
	public void serializeAssociationDefinition(AssociationDefinition associationDefinition,
			final SerializationSettings format, final JsonGenerator generator) throws IOException;

	/**
	 * Serializes information on all {@link DataTypeDefinition}s.
	 * 
	 * @param format
	 * @param generator
	 */
	public void serializeDataTypeDefinitions(SerializationSettings format, JsonGenerator generator) throws IOException;

	/**
	 * Serializes a {@link DataTypeDefinition}.
	 * 
	 * @param dataTypeDefinition
	 * @param format
	 * @param generator
	 * @throws IOException
	 */
	public void serializeDataTypeDefinition(final DataTypeDefinition dataTypeDefinition,
			final SerializationSettings format, final JsonGenerator generator) throws IOException;

}