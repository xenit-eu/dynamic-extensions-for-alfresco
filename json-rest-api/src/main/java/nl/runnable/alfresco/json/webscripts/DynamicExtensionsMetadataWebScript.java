package nl.runnable.alfresco.json.webscripts;

import java.io.IOException;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.json.DictionarySerializationService;
import nl.runnable.alfresco.json.DynamicExtensionsMetadataSerializationService;
import nl.runnable.alfresco.metadata.MetadataRegistry;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.core.JsonGenerator;

@ManagedBean
@WebScript
public class DynamicExtensionsMetadataWebScript extends AbstractJsonWebScript {

	/* Dependencies */

	@Inject
	private MetadataRegistry metadataRegistry;

	@Inject
	private DynamicExtensionsMetadataSerializationService serializationService;

	@Inject
	private DictionarySerializationService dictionarySerializationService;

	/* Operations */

	@Uri("/json-api/dynamic-extensions/metadata")
	public void getMetadata(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
		final JsonGenerator generator = createJsonGenerator(response, createSerializationSettings(request));
		serializationService.serializeDynamicExtensionsMetadata(metadataRegistry.getAllMetadata(), generator);
		generator.close();
	}

	public void setDictionarySerializationService(final DictionarySerializationService dictionarySerializationService) {
		this.dictionarySerializationService = dictionarySerializationService;
	}

	protected DictionarySerializationService getDictionarySerializationService() {
		return dictionarySerializationService;
	}

}
