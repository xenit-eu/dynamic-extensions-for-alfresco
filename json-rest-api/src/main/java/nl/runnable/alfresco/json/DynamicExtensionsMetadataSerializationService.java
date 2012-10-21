package nl.runnable.alfresco.json;

import java.io.IOException;
import java.util.Collection;

import nl.runnable.alfresco.metadata.ExtensionMetadata;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Defines operations for serializing Dynamic Extensions {@link ExtensionMetadata}.
 * 
 * @author Laurens Fridael
 * 
 */
public interface DynamicExtensionsMetadataSerializationService {

	void serializeDynamicExtensionsMetadata(Collection<ExtensionMetadata> metadata, JsonGenerator generator) throws IOException;

}
