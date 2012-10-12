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

import nl.runnable.alfresco.osgi.BundleState;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;

/**
 * Value Object representing information from a {@link Bundle}. This class adapts {@link Bundle} information to a format
 * suitable for use in {@link WebScript} responses.
 * 
 * @author Laurens Fridael
 * 
 */
public class BundleInfo implements Comparable<BundleInfo> {

	private final Bundle bundle;

	public BundleInfo(final Bundle bundle) {
		Assert.notNull(bundle, "Bundle cannot be null.");
		this.bundle = bundle;
	}

	protected Bundle getBundle() {
		return bundle;
	}

	public long getId() {
		return getBundle().getBundleId();
	}

	public String getState() {
		final BundleState bundleState = BundleState.fromStateId(getBundle().getState());
		return (bundleState != null ? bundleState.name() : null);
	}

	public String getSymbolicName() {
		return getBundle().getSymbolicName();
	}

	public String getVersion() {
		return getBundle().getVersion().toString();
	}

	public String getName() {
		return getBundle().getHeaders().get(Constants.BUNDLE_NAME);
	}

	public String getDescription() {
		return getBundle().getHeaders().get(Constants.BUNDLE_DESCRIPTION);
	}

	public String getVendor() {
		return getBundle().getHeaders().get(Constants.BUNDLE_VENDOR);
	}

	/**
	 * Compares {@link BundleInfo} instances by symbolic name and version in ascending order.
	 */
	@Override
	public int compareTo(final BundleInfo o) {
		int compare;
		if (o != null) {
			compare = getSymbolicName().compareTo(o.getSymbolicName());
			if (compare == 0) {
				compare = getVersion().compareTo(o.getVersion());
			}
		} else {
			compare = 1;
		}
		return compare;
	}

}
