package eu.xenit.dynamicextensionsalfresco;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Versions {

    public static final String alfrescoEdition = "community";

    public static final Map<String, String> alfrescoVersions = new HashMap<String, String>() {{
        put("50", "5.0.d");
        put("52", "5.2.f");
    }};

    public static final Set<String> supportedAlfrescoVersions = alfrescoVersions.keySet();

    public static final Map<String, String> springVersions = new HashMap<String, String>() {{
        put("50", "3.2.10.RELEASE");
        put("52", "3.2.17.RELEASE");
    }};

    public static String getSimpleAlfrescoVersionFromProjectName(final String projectName) {
        for (final String shortVersion : alfrescoVersions.keySet()) {
            if (projectName.endsWith(shortVersion)) {
                // Subproject for a specific Alfresco version
                return shortVersion;
            }
        }
        return "50";

    }
}
