package nl.runnable.alfresco.management;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.metadata.ExtensionMetadata;
import nl.runnable.alfresco.metadata.MetadataRegistry;
import nl.runnable.alfresco.metadata.ModelMetadata;
import nl.runnable.alfresco.webscripts.annotations.Transaction;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

@ManagedBean
@WebScript(description = "Provides info on Dynamic Extensions.")
@Transaction(readOnly = true)
public class InfoWebScript extends AbstractJsonWebScript {

	/* Dependencies */

	@Inject
	private MetadataRegistry metadataRegistry;

	/* Main operations */

	@Uri("/dynamic-extensions/management/info")
	public void getInfo(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final JsonGenerator generator = createJsonGenerator(request, response);
		writeInfo(generator);
	}

	/* Utility operations */

	private void writeInfo(final JsonGenerator generator) throws IOException {
		generator.writeStartObject();
		writeFileInstallPaths(generator);
		writeExtensions(generator);
		generator.writeEndObject();
		generator.close();
	}

	private void writeFileInstallPaths(final JsonGenerator generator) throws IOException {
		generator.writeArrayFieldStart("fileInstallPaths");
		for (final String path : metadataRegistry.getContainerMetadata().getFileInstallPaths()) {
			generator.writeString(path);
		}
		generator.writeEndArray();
	}

	private void writeExtensions(final JsonGenerator generator) throws IOException, JsonGenerationException {
		generator.writeArrayFieldStart("extensions");
		for (final ExtensionMetadata extension : metadataRegistry.getExtensionsMetadata()) {
			generator.writeStartObject();
			generator.writeStringField("symbolicName", extension.getName());
			generator.writeStringField("version", extension.getVersion());
			generator.writeBooleanField("isCoreBundle", extension.isCoreBundle());
			generator.writeArrayFieldStart("models");
			for (final ModelMetadata model : extension.getModels()) {
				generator.writeStartObject();
				generator.writeStringField("name", model.getName());
				// generator.writeStringField("version", model.getVersion());
				generator.writeStringField("description", model.getDescription());
				generator.writeEndObject();
			}
			generator.writeEndArray();
			generator.writeEndObject();
		}
		generator.writeEndArray();
	}

}
