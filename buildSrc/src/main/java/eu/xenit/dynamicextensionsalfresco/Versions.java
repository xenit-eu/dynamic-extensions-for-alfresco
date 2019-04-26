package eu.xenit.dynamicextensionsalfresco;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Versions {

    public static final String alfrescoEdition = "community";

    public static final Map<String, String> alfrescoVersions = new HashMap<String, String>() {{
        put("50", "5.0.d");
        put("51", "5.1.g");
        put("52", "5.2.f");
        put("60", "6.0.7-ga");
        put("61", "6.1.2-ga");
    }};

    public static final Set<String> supportedAlfrescoVersions = alfrescoVersions.keySet();

    public static final Map<String, String> springVersions = new HashMap<String, String>() {{
        put("50", "3.2.10.RELEASE");
        put("51", "3.2.14.RELEASE");
        put("52", "3.2.17.RELEASE");
        put("60", "5.0.4.RELEASE");
        put("61", "5.1.3.RELEASE");
    }};

    public static final Map<String, String> geminiVersions = new HashMap<String, String>() {{
        put("50", "1.0.2.RELEASE");
        put("51", "1.0.2.RELEASE");
        put("52", "1.0.2.RELEASE");
        put("60", "3.0.0.M01");
        put("61", "3.0.0.M01");
    }};

    public static String getSimpleAlfrescoVersionFromProjectName(final String projectName) {
        for (final String shortVersion : alfrescoVersions.keySet()) {
            if (projectName.endsWith(shortVersion)) {
                // Subproject for a specific Alfresco version
                return shortVersion;
            }
        }
        // Return a default version for project that require an Alfresco dependency for javadoc only
        return "50";

    }
}
