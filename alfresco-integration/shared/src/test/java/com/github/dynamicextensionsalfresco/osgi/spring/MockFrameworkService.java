package com.github.dynamicextensionsalfresco.osgi.spring;

import com.github.dynamicextensionsalfresco.osgi.FrameworkManager;
import com.github.dynamicextensionsalfresco.osgi.FrameworkService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;

import static org.mockito.Mockito.*;

/**
 * @author Laurent Van der Linden
 */
public class MockFrameworkService implements FrameworkService {
	@Override
	public void restartFramework() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public FrameworkManager getFrameworkManager() {
		final FrameworkManager frameworkManagerMock = mock(FrameworkManager.class);
		final Framework frameworkMock = mock(Framework.class);
		final BundleContext contextMock = mock(BundleContext.class);

		when(frameworkManagerMock.getFramework()).thenReturn(frameworkMock);
		when(frameworkMock.getBundleContext()).thenReturn(contextMock);

		try {
			final ServiceReference serviceReferenceMock = mock(ServiceReference.class);
			when(contextMock.getAllServiceReferences(null, "(&(objectClass=java.lang.Runnable)(objectClass=java.lang.reflect.InvocationHandler))"))
				.thenReturn(new ServiceReference[]{serviceReferenceMock});
			when(contextMock.getService(serviceReferenceMock)).thenReturn(new DummyService());
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}

		return frameworkManagerMock;
	}
}
