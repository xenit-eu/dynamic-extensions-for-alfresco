package nl.runnable.alfresco.json;

import java.io.IOException;
import java.util.Collection;

import nl.runnable.alfresco.metadata.Metadata;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Defines operations for serializing Dynamic Extensions {@link Metadata}.
 * 
 * @author Laurens Fridael
 * 
 */
public interface DynamicExtensionsMetadataSerializationService {

	void serializeDynamicExtensionsMetadata(Collection<Metadata> metadata, JsonGenerator generator) throws IOException;

}
