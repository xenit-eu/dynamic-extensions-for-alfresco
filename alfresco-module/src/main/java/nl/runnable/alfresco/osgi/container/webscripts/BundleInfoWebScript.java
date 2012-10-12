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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.StringUtils;

/**
 * Obtains information on the Framework Bundle and any managed Bundles.
 * 
 * @author Laurens Fridael
 * 
 */
public class BundleInfoWebScript extends AbstractBundleWebScript {

	/* Request handling */

	@Override
	public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final String bundleId = request.getServiceMatch().getTemplateVars().get("bundleId");
		if (StringUtils.hasText(bundleId)) {
			try {
				final JSONObject responseObject = getBundleInfo(Long.parseLong(bundleId));
				if (responseObject != null) {
					response.setContentType("application/json");
					response.getWriter().write(responseObject.toString());
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					writeResponseMessage(response, String.format("Cannot find managed Bundle with ID %s", bundleId));
				}
			} catch (final NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writeResponseMessage(response,
						String.format("Cannot parse Bundle ID: %s. Value must be a number.", bundleId));
				return;
			}
		} else {
			response.setContentType("application/json");
			response.getWriter().write(getBundlesInfo(response).toString());
		}
	}

	/* Utility operations */

	/**
	 * Obtains information on a Bundle identified by a Bundle ID.
	 * 
	 * @param bundleId
	 *            The Bundle ID with 0 denoting the Framework bundle.
	 * @return A corresponding {@link JSONObject} or null if the Bundle could not be found.
	 */
	protected JSONObject getBundleInfo(final long bundleId) {
		final BundleInfo bundleInfo;
		if (bundleId == 0) {
			bundleInfo = getBundleWebScriptService().getFrameworkBundle();
		} else {
			bundleInfo = getBundleWebScriptService().getManagedBundle(bundleId);
		}
		if (bundleInfo != null) {
			return new JSONObject(bundleInfo);
		} else {
			return null;
		}
	}

	/**
	 * Obtains information on the Framework Bundle and all managed Bundles in that order.
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	protected JSONArray getBundlesInfo(final WebScriptResponse response) throws IOException {
		final List<JSONObject> bundles = new ArrayList<JSONObject>();
		bundles.add(new JSONObject(getBundleWebScriptService().getFrameworkBundle()));
		for (final BundleInfo bundle : getBundleWebScriptService().getManagedBundles()) {
			bundles.add(new JSONObject(bundle));
		}
		return new JSONArray(bundles);
	}

}
