package com.github.dynamicextensionsalfresco.osgi.spring;

import com.github.dynamicextensionsalfresco.proxy.FilterModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;

/**
 * Verify proxy can be cast to all types (user specified + {@link FilterModel}) and we can invoke the proxy.
 *
 * @author Laurent Van der Linden
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com/github/dynamicextensionsalfresco/osgi/spring/BundleServiceProxyTest.xml" })
public class BundleServiceProxyTest {
	@Autowired
	@Resource(name = "someDynamicService")
	public Runnable someDynamicService = null;

	@Test
	public void testServiceProxy() {
		Assert.assertTrue("runner should be InvocationHandler", someDynamicService instanceof InvocationHandler);
		Assert.assertTrue("runner should be FilterModel", someDynamicService instanceof FilterModel);

		try {
			InvocationHandler invocationHandler = (InvocationHandler)someDynamicService;
			Assert.assertEquals("Daft result", invocationHandler.invoke(null, null, null));
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
