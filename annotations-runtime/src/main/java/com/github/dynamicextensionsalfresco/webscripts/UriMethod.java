package com.github.dynamicextensionsalfresco.webscripts;

import org.springframework.extensions.webscripts.UriIndex;

/**
 * Key for looking up {@link UriIndex} mappings used by {@link WebScriptUriRegistry}.
 * 
 * @author Laurens Fridael
 * 
 */
class UriMethod {

	static UriMethod forUriAndMethod(final String uri, final String method) {
		return new UriMethod(uri, method);
	}

	private final String uri;

	private final String method;

	private UriMethod(final String uri, final String method) {
		this.uri = uri;
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public String getMethod() {
		return method;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UriMethod other = (UriMethod) obj;
		if (method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!method.equals(other.method)) {
			return false;
		}
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}

}
