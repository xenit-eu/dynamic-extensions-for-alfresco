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

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

public class SystemPackageEditor extends PropertyEditorSupport {

	// TODO: Fine-tune regular expressions.

	private static final Pattern PACKAGE_AND_VERSION_PATTERN = Pattern.compile(
			"([a-z0-9\\._]+)\\s*;\\s*(([0-9\\.]+)+)", Pattern.CASE_INSENSITIVE);

	private static final Pattern PACKAGE_ONLY_PATTERN = Pattern.compile("[a-z0-9\\._]+", Pattern.CASE_INSENSITIVE);

	private String defaultVersion = "1.0";

	public void setDefaultVersion(final String defaultVersion) {
		Assert.hasText(defaultVersion);
		this.defaultVersion = defaultVersion;
	}

	protected String getDefaultVersion() {
		return defaultVersion;
	}

	@Override
	public void setAsText(final String text) throws IllegalArgumentException {
		Matcher matcher = PACKAGE_AND_VERSION_PATTERN.matcher(text);
		if (matcher.matches()) {
			final String name = matcher.group(1);
			final String version = matcher.group(3);
			setValue(new SystemPackage(name, version));
		} else {
			matcher = PACKAGE_ONLY_PATTERN.matcher(text);
			if (matcher.matches()) {
				setValue(new SystemPackage(text, getDefaultVersion()));
			} else {
				throw new IllegalArgumentException(String.format("Unrecognized value: %s", text));
			}
		}
	}

}
