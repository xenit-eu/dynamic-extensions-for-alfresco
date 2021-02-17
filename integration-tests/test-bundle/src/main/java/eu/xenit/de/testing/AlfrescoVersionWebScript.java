package eu.xenit.de.testing;

import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_BASE_URI;
import static eu.xenit.de.testing.Constants.TEST_WEBSCRIPTS_FAMILY;

import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@WebScript(families = TEST_WEBSCRIPTS_FAMILY, baseUri = TEST_WEBSCRIPTS_BASE_URI)
@SuppressWarnings("unused")
public class AlfrescoVersionWebScript {

    @Autowired
    private DescriptorService descriptorService;

    @Uri("/alfresco-version")
    @ResponseBody
    public AlfrescoVersion getAlfrescoVersion() {
        return new AlfrescoVersion(descriptorService.getCurrentRepositoryDescriptor());
    }

    private static class AlfrescoVersion {

        private final String version;
        private final int major;
        private final int minor;
        private final int revision;

        AlfrescoVersion(Descriptor descriptor) {
            this.version = descriptor.getVersion();
            this.major = Integer.parseInt(descriptor.getVersionMajor());
            this.minor = Integer.parseInt(descriptor.getVersionMinor());
            this.revision = Integer.parseInt(descriptor.getVersionRevision());
        }

        public String getVersion() {
            return version;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getRevision() {
            return revision;
        }
    }


}
