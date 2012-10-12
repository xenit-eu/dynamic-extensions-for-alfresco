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

package nl.runnable.alfresco.repository.node;

import java.io.Serializable;
import java.util.Collection;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * Defines helper operations for working with repository nodes. Intended as a companion to the Alfresco
 * {@link NodeService}.
 * 
 * @author Laurens Fridael
 * 
 */
public interface NodeHelper {
	/**
	 * Obtains the path of a node in XPath format.
	 * 
	 * @param nodeRef
	 *            The NodeRef.
	 * @return The NodeRef path.
	 * @see PathHelper
	 */
	public String getPath(NodeRef nodeRef);

	/**
	 * Obtains the values for a multi-valued property of a node. If the property is single-valued (i.e. multiplicity of
	 * one), this method wraps the value in a Collection with a single element.
	 * 
	 * @param nodeRef
	 *            The NodeRef.
	 * @param property
	 *            The property.
	 * @return The property values as a Collection or null if the property's value is null or the node does not contain
	 *         the property.
	 */
	public <T extends Serializable> Collection<T> getPropertyValues(NodeRef nodeRef, QName property);

	/**
	 * Determines if a node is of a given type.
	 * 
	 * @param nodeRef
	 *            The NodeRef.
	 * @param type
	 *            The type QName.
	 * @return True if the node is of the given type, false if not.
	 */
	public boolean isOfType(NodeRef nodeRef, QName type);

	/**
	 * Obtains the primary parent node.
	 * 
	 * @param nodeRef
	 * @return
	 */
	public NodeRef getPrimaryParent(NodeRef nodeRef);

	/**
	 * Obtains the node for Company Home.
	 * 
	 * @return
	 */
	public NodeRef getCompanyHome();
}
