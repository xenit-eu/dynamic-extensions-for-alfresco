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

package nl.runnable.alfresco.json;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.Assert;

/**
 * Represents settings for serialization operations used by {@link NodeSerializationService} and
 * {@link DictionarySerializationService}.
 * 
 * @author Laurens Fridael
 * 
 */
public class SerializationSettings {

	/**
	 * Determines how to format QNames.
	 */
	private QNameFormat qNameFormat = QNameFormat.getDefault();

	/**
	 * Determines whether to obtain any child node information to a given depth. This setting is useful for retrieving
	 * folder trees in one operation.
	 * <p>
	 * The default value of 0 obtains no child node information.
	 */
	private int childDepth = 0;

	/**
	 * The base path for calculating REST locations. This is typically set to the current {@link WebScriptRequest}'s
	 * service context path.
	 * 
	 * @see WebScriptRequest#getServiceContextPath()
	 */
	private String basePath = "";

	/**
	 * Determines whether JSON output is pretty-printed.
	 */
	private boolean prettyPrint = false;

	public void setQNameFormat(final QNameFormat qNameFormat) {
		Assert.notNull(qNameFormat, "QName format cannot be null.");
		this.qNameFormat = qNameFormat;
	}

	public QNameFormat getQNameFormat() {
		return qNameFormat;
	}

	public void setChildDepth(final int childDepth) {
		Assert.isTrue(childDepth >= 0, "Child depth must be 0 or greater.");
		this.childDepth = childDepth;
	}

	public int getChildDepth() {
		return childDepth;
	}

	public void setBasePath(final String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setPrettyPrint(final boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

}
