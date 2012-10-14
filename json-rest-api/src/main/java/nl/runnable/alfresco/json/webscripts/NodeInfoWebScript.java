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

import nl.runnable.alfresco.annotations.AlfrescoService;
import nl.runnable.alfresco.annotations.ServiceType;
import nl.runnable.alfresco.json.NodeSerializationService;
import nl.runnable.alfresco.json.SerializationSettings;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Handles Web Script requests for node information.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript
public class NodeInfoWebScript extends AbstractJsonWebScript {

	/* Dependencies */

	@Inject
	@AlfrescoService(ServiceType.LOW_LEVEL)
	private NodeService nodeService;

	@Inject
	private NodeSerializationService nodeSerializationService;

	/* Main Operations */

	@Uri(value = "/json-api/nodes/{protocol}/{store}/{id}")
	public void getNode(@UriVariable("protocol") final String protocol, @UriVariable("store") final String store,
			@UriVariable("id") final String id, final WebScriptRequest request, final WebScriptResponse response)
			throws IOException {
		final NodeRef nodeRef = new NodeRef(protocol, store, id);
		if (nodeNotFound(nodeRef, response)) {
			return;
		}
		writeNodeInfo(nodeRef, request, response);
	}

	@Uri(value = "/json-api/nodes/{protocol}/{store}")
	public void getRootNode(@UriVariable("protocol") final String protocol, @UriVariable("store") final String store,
			final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final NodeRef nodeRef = getNodeService().getRootNode(new StoreRef(protocol, store));
		writeNodeInfo(nodeRef, request, response);
	}

	/* Utility operations */

	/**
	 * Tests if the given {@link NodeRef} exists and outputs a 404 Not Found response if it does not.
	 * 
	 * @param nodeRef
	 * @param response
	 * @return True if the node was not found, false if it was.
	 * @throws IOException
	 */
	protected boolean nodeNotFound(final NodeRef nodeRef, final WebScriptResponse response) throws IOException {
		final boolean exists = getNodeService().exists(nodeRef);
		if (exists == false) {
			writeJsonMessage(404, String.format("Could not find node %s", nodeRef), response);
		}
		return !exists;
	}

	protected void writeNodeInfo(final NodeRef nodeRef, final WebScriptRequest request, final WebScriptResponse response)
			throws IOException {
		final SerializationSettings settings = createSerializationSettings(request);
		final JsonGenerator generator = createJsonGenerator(response, settings);
		getNodeSerializationService().serializeNode(nodeRef, settings, generator);
		generator.close();
	}

	/* Dependencies */

	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	public void setNodeSerializationService(final NodeSerializationService serializationService) {
		this.nodeSerializationService = serializationService;
	}

	protected NodeSerializationService getNodeSerializationService() {
		return nodeSerializationService;
	}
}
