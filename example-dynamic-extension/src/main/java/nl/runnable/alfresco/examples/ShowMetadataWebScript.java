package nl.runnable.alfresco.examples;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.metadata.Metadata;
import nl.runnable.alfresco.metadata.MetadataRegistry;
import nl.runnable.alfresco.metadata.Model;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.extensions.webscripts.WebScriptResponse;

@ManagedBean
@WebScript(description = "Shows Dynamic Extensions metadata")
public class ShowMetadataWebScript {
	@Inject
	private MetadataRegistry metadataRegistry;

	@Uri("/dynamic-extensions/metadata")
	public void showMetadata(final WebScriptResponse response) throws IOException {
		final Writer out = response.getWriter();
		for (final Metadata metadata : metadataRegistry.getAllMetadata()) {
			out.write(String.format("%s %s\n", metadata.getName(), metadata.getVersion()));
			for (final Model model : metadata.getModels()) {
				out.write(String.format("\t%s\n", model.getName()));
			}
		}
	}
}
