package com.github.dynamicextensionsalfresco.controlpanel;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Attribute;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.RedirectResolution;
import com.github.dynamicextensionsalfresco.webscripts.resolutions.Resolution;
import org.springframework.stereotype.Component;

/**
 * Handles requests for the index page.
 * <p>
 * This implementation redirects to the {@link Bundles} index.
 * 
 * @author Laurens Fridael
 */
@Component
@WebScript(families = "control panel")
public class IndexHandler extends AbstractControlPanelHandler {
	@Uri("/dynamic-extensions/")
	public Resolution redirectToBundles(@Attribute final ResponseHelper response) {
		return new RedirectResolution(Urls.BUNDLES);
	}
}
