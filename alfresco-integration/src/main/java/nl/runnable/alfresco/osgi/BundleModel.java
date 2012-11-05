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

package nl.runnable.alfresco.osgi;

import org.alfresco.service.namespace.QName;

/**
 * Contains constants representing types and properties from the Bundle model.
 * 
 * @author Laurens Fridael
 * 
 */
public class BundleModel {

	public static final String NAMESPACE_URI = "http://www.googlecode.com/alfresco-repository-extensions/model/osgi/bundle/1.0";

	public static final String NAMESPACE_PREFIX = "obm";

	// ----------------------------------------------------------------------------------------------------------------

	public static final QName TYPE_BUNDLE_FOLDER = QName.createQName(NAMESPACE_URI, "bundleFolder");

	/**
	 * @deprecated No longer relevant.
	 */
	@Deprecated
	public static final QName PROP_AUTOSTART_BUNDLES = QName.createQName(NAMESPACE_URI, "autoStartBundles");

	/**
	 * @deprecated No longer relevant.
	 */
	@Deprecated
	public static final QName PROP_BUNDLE_TYPE = QName.createQName(NAMESPACE_URI, "bundleType");

	// ----------------------------------------------------------------------------------------------------------------

	public static final QName TYPE_BUNDLE = QName.createQName(NAMESPACE_URI, "bundle");

	public static final QName PROP_SYMBOLIC_NAME = QName.createQName(NAMESPACE_URI, "symbolicName");

	public static final QName PROP_VERSION = QName.createQName(NAMESPACE_URI, "version");

	// ----------------------------------------------------------------------------------------------------------------

	public static final QName TYPE_MANAGED_BUNDLE = QName.createQName(NAMESPACE_URI, "managedBundle");

	public static final QName PROP_BUNDLE_ID = QName.createQName(NAMESPACE_URI, "bundleId");

	public static final QName PROP_STATE = QName.createQName(NAMESPACE_URI, "state");

	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Constructor made private to prevent instantiation and subclassing.
	 */
	private BundleModel() {
	}
}
