#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.io.IOException;

import javax.annotation.ManagedBean;

import nl.runnable.alfresco.webscripts.annotations.RequestParam;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Example annotation-based Web Script.
 * <p>
 * Explore the <a href="https://github.com/lfridael/${artifactId}">Example Dynamic Extension</a>
 * to find out more about annotation-based Web Scripts.
 */
@ManagedBean
@WebScript
public class HelloWebScript {
  /**
    GET http://localhost:8080/alfresco/service/${packageInPathFormat}/${artifactId}/hello?name=Alfresco
  */
	@Uri("/${packageInPathFormat}/${artifactId}/hello")
	public void hello(@RequestParam(defaultValue = "Alfresco") final String name, final WebScriptResponse response) throws IOException {
		final String message = String.format("Hello, %s", name);
		response.getWriter().write(message);
	}
}
