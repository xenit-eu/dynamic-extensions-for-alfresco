package com.github.dynamicextensionsalfresco.controlpanel;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.github.dynamicextensionsalfresco.controlpanel.BundleHelper;
import com.github.dynamicextensionsalfresco.controlpanel.BundleHelperTest.ThrowingFunction;
import com.github.dynamicextensionsalfresco.controlpanel.BundleIdentifier;
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService;
import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.internal.StandardBundleManifest;
import com.springsource.util.osgi.manifest.parse.DummyParserLogger;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.FrameworkWiring;
import org.springframework.extensions.webscripts.Container;

class MockBundleHelper extends BundleHelper {

    private boolean update;
    private ThrowingFunction<Bundle, BundleContext, BundleException> mockBundle;

    public MockBundleHelper(BundleContext bundleContext,
            RepositoryStoreService repositoryStoreService,
            FileFolderService fileFolderService,
            ContentService contentService,
            NodeService nodeService,
            Container webScriptsContainer,
            boolean update,
            ThrowingFunction<Bundle, BundleContext, BundleException> mockBundleProvider) {
        super(bundleContext, repositoryStoreService, fileFolderService, contentService, nodeService,
                webScriptsContainer);
        this.update = update;
        this.mockBundle = mockBundleProvider;
    }

    @Override
    public BundleIdentifier getBundleIdentifier(File tempFile) {
        return BundleIdentifier.fromSymbolicNameAndVersion("test-bundle", "1.0");
    }

    @Override
    public String getBundleRepositoryLocation() {
        return "/app:any";
    }

    @Override
    public InputStream createStreamForFile(File file) {
        return new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public Boolean isFragmentBundle(Bundle bundle) {
        return false;
    }

    @Override
    public BundleManifest createBundleManifest(Bundle bundle) {
        return new StandardBundleManifest(new DummyParserLogger());
    }

    @Override
    public void saveBundleInRepository(File file, String filename, BundleManifest manifest) {
    }

    @Override
    public void resetWebScriptsCache() {
    }

    @Override
    public Bundle findBundleBySymbolicName(BundleIdentifier identifier) throws BundleException {
        return update ? mockBundle.get(this.getBundleContext()) : null;
    }

    @Override
    public NodeRef uninstallAndDeleteBundle(Bundle bundle) {
        return null;
    }

    @Override
    protected FrameworkWiring getFrameworkWiring() {
        FrameworkWiring wiring = mock(FrameworkWiring.class);
        ArgumentCaptor<FrameworkListener> frameworkListener = ArgumentCaptor.forClass(FrameworkListener.class);
        doAnswer(invocation -> {
            frameworkListener.getValue().frameworkEvent(
                    new FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(Bundle.class), null)
            );
            return invocation;
        })
                .when(wiring).refreshBundles(anyObject(), frameworkListener.capture());
        return wiring;
    }
}
