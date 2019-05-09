package com.github.dynamicextensionsalfresco.controlpanel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.dynamicextensionsalfresco.event.EventListener;
import com.github.dynamicextensionsalfresco.event.events.SpringContextException;
import com.github.dynamicextensionsalfresco.event.impl.DefaultEventBus;
import com.github.dynamicextensionsalfresco.osgi.RepositoryStoreService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.osgi.framework.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import java.io.File;
import java.io.InputStream;

public class BundleHelperTest {

    @Test(expected = BundleException.class)
    public void bundleWithInvalidManifest() throws BundleException, InvalidSyntaxException {
        Actors actors = stageActors(false, this::defaultMockBundleProvider);
        when(actors.getBundleContext().installBundle(anyString(), anyObject()))
                .thenThrow(new BundleException("cannot resolve some crazy import"));
        actors.getBundleHelper().doInstallBundleInRepository(new File("."), ".");
    }

    @Test(expected = BeansException.class)
    public void bundleWithInvalidSpringConfig() throws BundleException, InvalidSyntaxException {
        Actors actors = stageActors(false, this::defaultMockBundleProvider);

        when(actors.getBundleContext().installBundle(anyString(), any(InputStream.class))).then(invocation -> {
            SpringContextException springContextException
                    = new SpringContextException(
                    mock(Bundle.class),
                    new ApplicationContextException("Spring could not autowire some stuff"));
            new DefaultEventBus(actors.getBundleContext()).publish(springContextException);
            return mock(Bundle.class);
        });

        actors.getBundleHelper().doInstallBundleInRepository(new File("."), "any");
    }

    @Test
    public void installableBundle() throws BundleException, InvalidSyntaxException {
        Actors actors = stageActors(false, this::defaultMockBundleProvider);

        when(actors.getBundleContext().installBundle(anyString(), any(InputStream.class))).then(invocation -> {
            actors.getFrameworkListener().frameworkEvent(
                    new FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED, mock(Bundle.class), null));
            return mock(Bundle.class);
        });

        actors.getBundleHelper().doInstallBundleInRepository(new File("."), "any");
    }

    @Test(expected = BundleException.class)
    public void updateBundleWithInvalidManifest() throws BundleException, InvalidSyntaxException {
        Actors actors = stageActors(true, this::updateBundleWithInvalidManifest_mockBundleProvider);

        actors.getBundleHelper().doInstallBundleInRepository(new File("."), "any");
    }

    private Bundle updateBundleWithInvalidManifest_mockBundleProvider(BundleContext bc) throws BundleException {
        Bundle mockBundle = mock(Bundle.class);
        BundleException exception = new BundleException("failed to resolve test bundle",
                BundleException.RESOLVE_ERROR);
        doThrow(exception).when(mockBundle).start();
        return mockBundle;
    }

    @Test(expected = BeansException.class)
    public void updateBundleWithInvalidSpringConfig() throws InvalidSyntaxException, BundleException {
        Actors actors = stageActors(true, this::updateBundleWithInvalidSpringConfig_mockBundleProvider);

        actors.getBundleHelper().doInstallBundleInRepository(new File("."), "any");
    }

    private Bundle updateBundleWithInvalidSpringConfig_mockBundleProvider(BundleContext bundleContext) {
        Bundle mockBundle = mock(Bundle.class);
        try {
            doAnswer(invocation -> {
                SpringContextException springException = new SpringContextException(
                        mock(Bundle.class),
                        new ApplicationContextException("Spring could not autowire some stuff"));
                new DefaultEventBus(bundleContext).publish(springException);
                return invocation;
            }).when(mockBundle)
                    .start();
        } catch (BundleException e) {
            Assert.fail(e.getMessage());
        }
        return mockBundle;
    }

    @Test
    public void updateInstallableBundle() throws BundleException, InvalidSyntaxException {
        Actors actors = stageActors(true, this::defaultMockBundleProvider);

        FrameworkEvent frameworkEvent = new FrameworkEvent(FrameworkEvent.PACKAGES_REFRESHED,
                mock(Bundle.class),
                null);
        when(actors.getBundleContext().installBundle(anyString(), any(InputStream.class)))
                .then(invocation -> {
                    actors.getFrameworkListener().frameworkEvent(frameworkEvent);
                    return mock(Bundle.class);
                });

        actors.getBundleHelper().doInstallBundleInRepository(new File("."), "any");
    }

    private Actors stageActors(Boolean update,
            ThrowingFunction<Bundle, BundleContext, BundleException> mockBundleProvider
    ) throws InvalidSyntaxException {
        MockBundleHelper bundleHelper = new MockBundleHelper(mock(BundleContext.class),
                mock(RepositoryStoreService.class), mock(FileFolderService.class),
                mock(ContentService.class), mock(NodeService.class),
                mock(org.springframework.extensions.webscripts.Container.class),
                update, mockBundleProvider);

        bundleHelper.registerEventListeners();
        when(bundleHelper.getBundleContext().getBundles()).thenReturn(new Bundle[]{});
        when(bundleHelper.getBundleContext().getAllServiceReferences(eq(EventListener.class.getName()), anyString()))
                .thenReturn(new ServiceReference[]{mock(ServiceReference.class)});

        when((BundleHelper) bundleHelper.getBundleContext().getService(Matchers.<ServiceReference<?>>any()))
                .thenReturn(bundleHelper);

        return new Actors(bundleHelper, bundleHelper.getBundleContext(), bundleHelper);
    }

    private Bundle defaultMockBundleProvider(BundleContext bundleContext) {
        return mock(Bundle.class);
    }

    private class Actors {

        private final BundleHelper bundleHelper;
        private final BundleContext bundleContext;
        private final FrameworkListener frameworkListener;

        public Actors(BundleHelper bundleHelper, BundleContext bundleContext, FrameworkListener frameworkListener) {
            this.bundleHelper = bundleHelper;
            this.bundleContext = bundleContext;
            this.frameworkListener = frameworkListener;
        }

        public BundleHelper getBundleHelper() {
            return this.bundleHelper;
        }

        public BundleContext getBundleContext() {
            return this.bundleContext;
        }

        public FrameworkListener getFrameworkListener() {
            return this.frameworkListener;
        }
    }

    @FunctionalInterface
    interface ThrowingFunction<R, T, E extends Throwable> {

        R get(T parameter) throws E;
    }
}