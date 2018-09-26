package com.github.dynamicextensionsalfresco.proxy

import com.github.dynamicextensionsalfresco.osgi.FrameworkService
import org.osgi.framework.Filter
import org.springframework.beans.factory.FactoryBean

import org.apache.felix.framework.FilterImpl
import java.lang.reflect.Proxy

/**
 * Spring [FactoryBean] to allow registration of service proxies at the global Alfresco level.
 * The emitted proxy will delegate to a service implemented by a Dynamic Extension, if any.
 *
 * These proxies are not guaranteed to be available at all times (startup or bundle uninstall),
 * so users of these proxies should handle [IllegalStateException] explicitly.
 *
 * The [Filter] will select the appropriate target instance for the proxy.
 * If you do not define it explicitly, the targetInterfaces will be used to compose one.
 * (find a extension service, implementing all specified interfaces.
 *
 * Note, that the filter should be specific enough to limit any matches to 1 service: if more then 1 match is found,
 * an [IllegalStateException] will be throw.

 * @author Laurent Van der Linden
 */
public class BundleServiceProxyFactory : FactoryBean<Any> {
    var frameworkService: FrameworkService? = null

    var filter: Filter? = null
    var targetInterfaces: Array<Class<*>>? = null

    @Throws(Exception::class)
    override fun getObject(): Any {
        val tracker = Tracker(DefaultFilterModel(targetInterfaces, filter), frameworkService)

        val proxyClasses = arrayOfNulls<Class<out Any>>(targetInterfaces!!.size + 1)
        System.arraycopy(targetInterfaces, 0, proxyClasses, 0, targetInterfaces!!.size)
        proxyClasses[targetInterfaces!!.size] = FilterModel::class.java

        return Proxy.newProxyInstance(javaClass.classLoader, proxyClasses, ServiceInvocationHandler(tracker))
    }

    override fun getObjectType(): Class<*>? {
        if (targetInterfaces == null || targetInterfaces!!.size == 0) {
            return null
        } else {
            return targetInterfaces!![0]
        }
    }

    override fun isSingleton(): Boolean = true

    public fun setFilterString(filterString: String) {
		this.filter = FilterImpl(filterString)
    }
}
