package nl.runnable.alfresco.gradle


/**
 * Constants for the default versions for the various depencies.
 */

class Versions {
	/*
	 * It's good practice to avoid SNAPSHOT dependencies, so this plugin adds a dependency on the previous
	 * Dynamic Extension milestone.
	 */
	static final DYNAMIC_EXTENSIONS = "1.0.0.M4"
	
	static final ALFRESCO = "4.0.e"
	
	static final SURF = "1.2.0-M3"
}
