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

package nl.runnable.alfresco.examples;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import nl.runnable.alfresco.annotations.AlfrescoService;
import nl.runnable.alfresco.annotations.ServiceType;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.CategoryService.Depth;
import org.alfresco.service.cmr.search.CategoryService.Mode;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Annotated Web Script that recursively lists the categories in the "generalclassifiable" classification.
 * <p>
 * This class illustrates Web Script request handling and how to obtain Alfresco service dependencies, such as
 * {@link CategoryService} and {@link NodeService}.
 * <p>
 * The {@link ManagedBean} annotation causes this class to be instantiated by Spring's component scanning. The
 * {@link WebScript} annotation marks it as an annotated Web Script for Dynamic Extensions to further decorate with
 * request handling logic. Both annotations need to be present for a Web Script to work! (Technically you can also
 * configure Spring to consider {@link WebScript} annotated classes to be managed beans, obviating the need for
 * {@link ManagedBean}, but this is NOT recommended practice.)
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript(description = "Shows the entire category tree.")
public class CategoriesWebScript {

	/**
	 * Annotate dependencies with {@link Inject} to obtain references to Alfresco services. This is how you'd normally
	 * inject dependencies in a Spring application. There is nothing specific to Dynamic Extensions about this style of
	 * dependency injection.
	 * <p>
	 * The {@link AlfrescoService} annotation lets you control whether you want the default (high-level) implementation
	 * of a service or, as in this case, the low-level version. Low-level versions are typically faster, as they are not
	 * equipped with security, auditing or transaction advice. (As you might expect, not specifying
	 * {@link AlfrescoService} is equivalent to using {@link ServiceType#DEFAULT}.)
	 */
	@Inject
	@AlfrescoService(ServiceType.LOW_LEVEL)
	private CategoryService categoryService;

	/**
	 * You can also inject Alfresco dependencies by name using {@link Named}. This is NOT recommended practice, as it
	 * couples your code to the specifics of Alfresco's Spring bean configuration.
	 */
	@Inject
	@Named("nodeService")
	private NodeService nodeService;

	/**
	 * Note how the Web Script URI is mapped using an annotation. Annotated Web Scripts do not use XML configuration,
	 * eliminating the code sprawl associated with regular Web Scripts.
	 * 
	 * @param response
	 * @throws IOException
	 */
	@Uri("/dynamic-extensions/examples/categories")
	public void showCategories(final WebScriptResponse response) throws IOException {
		final Writer out = response.getWriter();
		final Collection<ChildAssociationRef> rootCategories = categoryService.getRootCategories(
				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, ContentModel.ASPECT_GEN_CLASSIFIABLE);
		for (final ChildAssociationRef childAssociationRef : rootCategories) {
			showCategory(childAssociationRef.getChildRef(), 0, out);
		}
	}

	/**
	 * Recursively output the category names.
	 * 
	 * @param categoryNodeRef
	 * @param level
	 * @param out
	 * @throws InvalidNodeRefException
	 * @throws IOException
	 */
	private void showCategory(final NodeRef categoryNodeRef, final int level, final Writer out)
			throws InvalidNodeRefException, IOException {
		for (int i = 0; i < level; i++) {
			out.write("\t");
		}
		out.write(String.format("%s\n", nodeService.getProperty(categoryNodeRef, ContentModel.PROP_NAME)));
		final Collection<ChildAssociationRef> childCategories = categoryService.getChildren(categoryNodeRef,
				Mode.SUB_CATEGORIES, Depth.IMMEDIATE);
		for (final ChildAssociationRef childAssociationRef : childCategories) {
			showCategory(childAssociationRef.getChildRef(), level + 1, out);
		}
	}
}
