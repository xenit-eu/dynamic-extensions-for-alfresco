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

package nl.runnable.alfresco.json.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.inject.Inject;

import nl.runnable.alfresco.json.QNameFormat;
import nl.runnable.alfresco.json.SerializationSettings;

import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;

/**
 * Convenience abstract base class for serialization services.
 * 
 * @author Laurens Fridael
 * 
 */
public class AbstractSerializationService {

	/* Dependencies */

	@Inject
	private NamespacePrefixResolver namespacePrefixResolver;

	/* Utility Operations */

	protected String formattedQName(final QName qName, final QNameFormat qNameFormat) {
		if (qName != null) {
			switch (qNameFormat) {
			case FULLY_QUALIFIED:
				return qName.toString();
			case PREFIX:
				return qName.toPrefixString(getNamespacePrefixResolver());
			}
		}
		return null;
	}

	protected String location(final SerializationSettings format, final Object... pathSegments) {
		try {
			final StringBuilder sb = new StringBuilder();
			sb.append(format.getBasePath());
			if (pathSegments.length > 0) {
				for (final Object additionalPath : pathSegments) {
					if (additionalPath instanceof QName) {
						sb.append(URLEncoder
								.encode(formattedQName((QName) additionalPath, QNameFormat.PREFIX), "utf-8"));
					} else if (additionalPath != null) {
						sb.append(additionalPath.toString());
					}

				}
			}
			return sb.toString();
		} catch (final UnsupportedEncodingException e) {
			// Should not occur.
			throw new RuntimeException(e);
		}
	}

	protected String date(final Date date) {
		if (date != null) {
			return String.format("/Date(%d)/", date.getTime());
		} else {
			return null;
		}
	}

	/* Dependencies */

	public void setNamespacePrefixResolver(final NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}

	protected NamespacePrefixResolver getNamespacePrefixResolver() {
		return namespacePrefixResolver;
	}

}
