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

package nl.runnable.alfresco.webscripts;

import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Resolves {@link NodeRef} arguments using configurable template variables.
 * 
 * @author Laurens Fridael
 * 
 */
public class NodeRefArgumentResolver extends AbstractTypeBasedArgumentResolver<NodeRef> {

	/* Configuration */

	private String storeVariable = "store";

	private String protocolVariable = "protocol";

	private String idVariable = "id";

	@Override
	protected Class<?> getExpectedArgumentType() {
		return NodeRef.class;
	}

	@Override
	protected NodeRef resolveArgument(final WebScriptRequest request, final WebScriptResponse response) {
		NodeRef nodeRef = null;
		final Map<String, String> templateVariables = request.getServiceMatch().getTemplateVars();
		final String store = templateVariables.get(getStoreVariable());
		final String protocol = templateVariables.get(getProtocolVariable());
		final String id = templateVariables.get(getIdVariable());
		if (StringUtils.hasText(store) && StringUtils.hasText(protocol) && StringUtils.hasText(id)) {
			nodeRef = new NodeRef(protocol, store, id);
		}
		return nodeRef;
	}

	/* Configuration */

	public void setProtocolVariable(final String protocolVariable) {
		Assert.hasText(protocolVariable);
		this.protocolVariable = protocolVariable;
	}

	protected String getProtocolVariable() {
		return protocolVariable;
	}

	public void setStoreVariable(final String storeVariable) {
		Assert.hasText(storeVariable);
		this.storeVariable = storeVariable;
	}

	protected String getStoreVariable() {
		return storeVariable;
	}

	public void setIdVariable(final String idVariable) {
		Assert.hasText(idVariable);
		this.idVariable = idVariable;
	}

	protected String getIdVariable() {
		return idVariable;
	}

}
