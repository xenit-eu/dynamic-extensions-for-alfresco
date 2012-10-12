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

package nl.runnable.alfresco.repository.query;

/**
 * Enum representing predefined queryable fields. See the <a href="http://wiki.alfresco.com/wiki/Search">Search page on
 * the Alfresco Wiki</a> for more information.
 * <p>
 * In most cases, the enum names are identical to the query field names. However, for word combinations such as
 * {@link #IS_NOT_NULL}, the enum names use underscores to separate words, while the actual field names in the query
 * have the words packed together. (In the case of {@link #IS_NOT_NULL} the corresponding field name is
 * &quot;ISNOTNULL&quot;.)
 * 
 * @author Laurens Fridael
 */
public enum Field {
	TEXT, ID, IS_ROOT("ISROOT"), IS_NODE("ISNODE"), TX, PARENT, PRIMARY_PARENT("PRIMARYPARENT"), QNAME, CLASS, TYPE, EXACT_TYPE(
			"EXACTTYPE"), ASPECT, EXACT_ASPECT("EXACTASPECT"), ALL, IS_UNSET("ISUNSET"), IS_NULL("ISNULL"), IS_NOT_NULL(
			"ISNOTNULL"), PATH, PATH_WITH_REPEATS;

	private String fieldName;

	/**
	 * Constructs an instance where the query field name and enum name are identical.
	 */
	private Field() {
		this.fieldName = name();
	}

	/**
	 * Constructs an instance where the query field name can be different from the enum name.
	 * 
	 * @param fieldName
	 */
	private Field(final String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Obtains the query field name.
	 * 
	 * @return The field name.
	 */
	public String getFieldName() {
		return fieldName;
	}
}
