package com.github.dynamicextensionsalfresco.models;

import java.io.IOException;

/**
 * @author Laurent Van der Linden
 */
public interface ModelRegistrar {
	void registerModels() throws IOException;

	void unregisterModels();
}
