package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;

import org.springframework.stereotype.Component;

/**
 * Handles requests for the index page.
 * <p>
 * This implementation redirects to the {@link Bundles} index.
 * 
 * @author Laurens Fridael
 */
@Component
@WebScript
public class IndexHandler extends AbstractControlPanelHandler {

	@Uri("/dynamic-extensions/")
	public void redirectToBundles(@Attribute final ResponseHelper response) {
		response.redirectToBundles();
	}
}
