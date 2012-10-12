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
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.annotations.AlfrescoService;
import nl.runnable.alfresco.annotations.ServiceType;
import nl.runnable.alfresco.json.NodeSerializationService;
import nl.runnable.alfresco.json.QNameFormat;
import nl.runnable.alfresco.json.SerializationSettings;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.springframework.util.Assert;

/**
 * {@link JsonGenerator}-based {@link NodeSerializationService} implementation.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
public class NodeSerializationServiceImpl extends AbstractSerializationService implements NodeSerializationService {

	/* Dependencies */

	@Inject
	@AlfrescoService(ServiceType.LOW_LEVEL)
	private NodeService nodeService;

	/* Main operations */

	public void serializeNode(final NodeRef nodeRef, final SerializationSettings format, final JsonGenerator generator)
			throws IOException {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		Assert.notNull(format, "SerializationSettings cannot be null.");
		Assert.notNull(generator, "JsonGenerator cannot be null.");

		serializeNode(nodeRef, format, generator, 0);
	}

	/* Node value operations */

	protected void serializeNode(final NodeRef nodeRef, final SerializationSettings format,
			final JsonGenerator generator, final int currentDepth) throws IOException, JsonGenerationException {
		generator.writeStartObject();
		generator.writeStringField(Fields.NODE_REF, nodeRef.toString());
		generator.writeStringField(Fields.TYPE,
				formattedQName(getNodeService().getType(nodeRef), format.getQNameFormat()));
		serializePropertyValues(nodeRef, format, generator);
		serializeChildAssociations(nodeRef, format, generator, currentDepth);
		generator.writeEndObject();
	}

	protected void serializePropertyValues(final NodeRef nodeRef, final SerializationSettings serializationSettings,
			final JsonGenerator generator) throws IOException, JsonGenerationException {
		generator.writeObjectFieldStart(Fields.PROPERTIES);
		for (final Entry<QName, Serializable> entry : getNodeService().getProperties(nodeRef).entrySet()) {
			serializePropertyValue(entry.getKey(), entry.getValue(), serializationSettings, generator);
		}
		generator.writeEndObject();
	}

	protected void serializePropertyValue(final QName name, Serializable value,
			final SerializationSettings serializationSettings, final JsonGenerator generator) throws IOException,
			JsonGenerationException {
		final QNameFormat qnameFormat = serializationSettings.getQNameFormat();
		final String fieldName = formattedQName(name, qnameFormat);
		if (value instanceof Number) {
			if (value instanceof Integer) {
				generator.writeNumberField(fieldName, ((Integer) value).intValue());
			} else if (value instanceof Long) {
				generator.writeNumberField(fieldName, ((Long) value).longValue());
			} else if (value instanceof Double) {
				generator.writeNumberField(fieldName, ((Double) value).doubleValue());
			} else if (value instanceof Float) {
				generator.writeNumberField(fieldName, ((Double) value).doubleValue());
			}
		} else if (value instanceof QName) {
			value = formattedQName((QName) value, qnameFormat);
			generator.writeStringField(fieldName, (String) value);
		} else if (value instanceof Date) {
			generator.writeStringField(fieldName, date((Date) value));
		} else if (value instanceof Collection) {
			@SuppressWarnings("unchecked")
			final Collection<Serializable> values = (Collection<Serializable>) value;
			generator.writeStartArray();
			for (final Serializable val : values) {
				serializePropertyValue(name, val, serializationSettings, generator);
			}
			generator.writeEndArray();
		} else if (value != null) {
			value = value.toString();
			generator.writeStringField(fieldName, (String) value);
		} else {
			generator.writeNullField(fieldName);
		}
	}

	protected void serializeChildAssociations(final NodeRef nodeRef, final SerializationSettings format,
			final JsonGenerator generator, final int currentDepth) throws IOException {
		generator.writeArrayFieldStart("childAssociations");
		for (final ChildAssociationRef childAssociationRef : getNodeService().getChildAssocs(nodeRef)) {
			generator.writeStartObject();
			final QName qName = childAssociationRef.getQName();
			if (qName != null) {
				generator.writeStringField("qName", formattedQName(qName, format.getQNameFormat()));
			} else {
				generator.writeNullField("qName");
			}
			final QName typeQName = childAssociationRef.getTypeQName();
			if (typeQName != null) {
				generator.writeStringField("typeQName", formattedQName(typeQName, format.getQNameFormat()));
			} else {
				generator.writeNullField("typeQName");
			}
			if (currentDepth < format.getChildDepth()) {
				generator.writeFieldName("node");
				serializeNode(childAssociationRef.getChildRef(), format, generator, currentDepth + 1);
			} else {
				generator.writeStringField("nodeRef", childAssociationRef.getChildRef().toString());
			}
			childAssociationRef.getChildRef();
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}

	/* Dependencies */

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

}
