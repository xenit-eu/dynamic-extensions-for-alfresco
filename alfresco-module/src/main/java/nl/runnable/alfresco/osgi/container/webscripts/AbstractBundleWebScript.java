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

package nl.runnable.alfresco.osgi.container.webscripts;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;

/**
 * Convenience abstract base class for {@link WebScript}s that use the {@link BundleWebScriptService}.
 * 
 * @author Laurens Fridael
 * 
 */
public abstract class AbstractBundleWebScript extends AbstractWebScript implements BundleWebScriptServiceAware {
	private BundleWebScriptService bundleWebScriptService;

	@Override
	public void setBundleWebScriptService(final BundleWebScriptService bundleWebScriptService) {
		this.bundleWebScriptService = bundleWebScriptService;
	}

	protected BundleWebScriptService getBundleWebScriptService() {
		return bundleWebScriptService;
	}

	protected void writeResponseMessage(final WebScriptResponse response, final String message) throws IOException {
		Assert.notNull(response, "Response cannot be null.");
		Assert.hasText(message, "Message cannot be empty.");

		try {
			final JSONObject responseObject = new JSONObject();
			responseObject.put("message", message);
			response.setContentType("application/json");
			response.getWriter().write(responseObject.toString());
		} catch (final JSONException e) {
			// Should not occur.
			throw new RuntimeException(e);
		}
	}
}
