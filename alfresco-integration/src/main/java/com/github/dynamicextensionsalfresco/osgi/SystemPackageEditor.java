package com.github.dynamicextensionsalfresco.osgi;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemPackageEditor extends PropertyEditorSupport {

	// TODO: Fine-tune regular expressions.

	private static final Pattern PACKAGE_AND_VERSION_PATTERN = Pattern.compile(
			"([a-z0-9\\._]+)\\s*;\\s*(([0-9\\.]+)+)", Pattern.CASE_INSENSITIVE);

	private static final Pattern PACKAGE_ONLY_PATTERN = Pattern.compile("[a-z0-9\\._]+", Pattern.CASE_INSENSITIVE);

	private String defaultVersion;

	public void setDefaultVersion(final String defaultVersion) {
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
