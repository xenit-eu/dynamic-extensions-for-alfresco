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

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Service that extracts {@link BundleManifestInfo} from JAR files.
 * 
 * @author Laurens Fridael
 */
public class BundleManifestInfoService {

	private static final String BUNDLE_VERSION_ATTRIBUTE = "Bundle-Version";

	private static final String BUNDLE_SYMBOLIC_NAME_ATTRIBUTE = "Bundle-SymbolicName";

	/**
	 * Parses OSGI bundle information from a JAR manifest.
	 * 
	 * @param jar
	 *            The JAR InputStream, which is closed automatically.
	 * @return The {@link BundleManifestInfo}.
	 * @throws IOException
	 *             // * @throws {@link MissingBundleManifestAttributeException} If the Manifest was missing required
	 *             bundle information.
	 */
	public BundleManifestInfo parseBundleManifestInfo(final JarInputStream jar) throws IOException {
		Assert.notNull(jar, "JAR InputStream cannot be null.");
		try {
			final Manifest manifest = jar.getManifest();
			final Attributes mainAttributes = manifest.getMainAttributes();
			final String symbolicName = mainAttributes.getValue(BUNDLE_SYMBOLIC_NAME_ATTRIBUTE);
			if (StringUtils.hasText(symbolicName) == false) {
				throw MissingBundleManifestAttributeException.forAttribute(BUNDLE_SYMBOLIC_NAME_ATTRIBUTE);
			}
			final String version = mainAttributes.getValue(BUNDLE_VERSION_ATTRIBUTE);
			if (StringUtils.hasText(version) == false) {
				throw MissingBundleManifestAttributeException.forAttribute(BUNDLE_VERSION_ATTRIBUTE);
			}
			return new BundleManifestInfo(symbolicName, version);
		} finally {
			try {
				jar.close();
			} catch (final IOException e) {
				// Ignore
			}
		}
	}
}
