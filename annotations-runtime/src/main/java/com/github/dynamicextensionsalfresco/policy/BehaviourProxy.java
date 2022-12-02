package com.github.dynamicextensionsalfresco.policy;

import com.github.dynamicextensionsalfresco.metrics.Timer;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.Policy;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Proxy that allows a {@link Behaviour} to be garbage-collected.
 *
 *
 * This class prevents dangling references to {@link Behaviour} instances when code is undeployed from an OSGi
 * container, as the {@link PolicyComponent} interface offers no means of unregistering {@link Behaviour}s. Dangling
 * references to the {@link BehaviourProxy} itself will continue to exist throughout the lifetime of the Alfresco
 * process, however. There will be a slight memory leak for every time you redeploy a Dynamic Extension that contains a
 * Behaviour. (Further revisions of this class may add the ability to reattach a Behaviour once a module gets updated.)
 *
 * @author Laurens Fridael
 */
public final class BehaviourProxy implements Behaviour {

    private final ConcurrentHashMap<Class<?>, ProxyPolicy> proxiesByPolicyClass;
    private Behaviour behaviour;
    @NotNull
    private final Timer timer;

    public BehaviourProxy(@NotNull Behaviour behaviour, @NotNull Timer timer) {
        if (behaviour == null) {
            throw new IllegalArgumentException("behaviour is null");
        }
        if (timer == null) {
            throw new IllegalArgumentException("timer is null");
        }

        this.behaviour = behaviour;
        this.timer = timer;
        this.proxiesByPolicyClass = new ConcurrentHashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInterface(@Nullable Class<T> policy) {
        if (policy == null) {
            return null;
        }
        ProxyPolicy proxyPolicy = proxiesByPolicyClass.get(policy);
        if (proxyPolicy == null) {
            if (behaviour instanceof NoOpBehaviour) {
                ProxyPolicyInvocationHandler proxyHandler = new ProxyPolicyInvocationHandler(null, this.behaviour,
                        this.timer);
                Object proxy = Proxy
                        .newProxyInstance(this.getClass().getClassLoader(), new Class[]{policy}, proxyHandler);
                proxyPolicy = new ProxyPolicy(proxy, proxyHandler);
            } else {
                Object originalHandler = this.behaviour.getInterface(policy);
                ProxyPolicyInvocationHandler proxyHandler = new ProxyPolicyInvocationHandler(originalHandler,
                        this.behaviour, this.timer);
                Object proxy = Proxy
                        .newProxyInstance(this.getClass().getClassLoader(), new Class[]{policy}, proxyHandler);
                proxyPolicy = new ProxyPolicy(proxy, proxyHandler);
            }
            proxiesByPolicyClass.put(policy, proxyPolicy);
        }
        return (T) proxyPolicy.getProxy();
    }

    /**
     * Clears the reference to the original {@link Behaviour} and clears the target references for the {@link
     * ProxyPolicyComponentInvocationHandler}s.
     */
    public final synchronized void release() {
        this.behaviour = new NoOpBehaviour(this.behaviour.getNotificationFrequency(), this.behaviour.isEnabled());
        for (ProxyPolicy proxyPolicy : proxiesByPolicyClass.values()) {
            proxyPolicy.handler.release();
        }
    }

    @Override
    @NotNull
    public String toString() {
        return this.behaviour.toString();
    }

    @NotNull
    public final Timer getTimer() {
        return this.timer;
    }

    @Override
    public void disable() {
        behaviour.disable();
    }

    @Override
    public void enable() {
        behaviour.enable();
    }

    @Override
    public boolean isEnabled() {
        return behaviour.isEnabled();
    }

    @Override
    public NotificationFrequency getNotificationFrequency() {
        return behaviour.getNotificationFrequency();
    }

    private static final class ProxyPolicyInvocationHandler implements InvocationHandler {

        private Object target;
        private Behaviour behaviour;
        @NotNull
        private final Timer timer;

        public ProxyPolicyInvocationHandler(@Nullable Object target, @Nullable Behaviour behaviour,
                @NotNull Timer timer) {
            if (timer == null) {
                throw new IllegalArgumentException("timer is null");
            }

            this.target = target;
            this.behaviour = behaviour;
            this.timer = timer;
        }

        @Override
        @Nullable
        public Object invoke(@NotNull Object proxy, @NotNull final Method method, @Nullable final Object[] args)
                throws Throwable {
            if (proxy == null) {
                throw new IllegalArgumentException("proxy is null");
            }
            if (method == null) {
                throw new IllegalArgumentException("method is null");
            }

            if (method.getDeclaringClass().isAssignableFrom(Object.class)) {
                // Direct Object methods to ourselves.
                if (args != null) {
                    return method.invoke(this, Arrays.copyOf(args, args.length));
                } else {
                    return method.invoke(this);
                }
            } else if (Policy.class.isAssignableFrom(method.getDeclaringClass())) {
                /* Policy interface operations always return void. */
                if (this.behaviour != null) {
                    try {
                        return timer.time(
                                () -> {
                                    StringBuilder labelBuilder = new StringBuilder();
                                    labelBuilder.append(behaviour.toString()).append(" ");

                                    if (args != null) {
                                        labelBuilder.append(
                                                Arrays.stream(args)
                                                        .filter(arg -> arg instanceof NodeRef)
                                                        .map(Object::toString)
                                                        .collect(Collectors.joining(",")));
                                    }
                                    return labelBuilder.toString();
                                },
                                () -> (args != null) ?
                                        method.invoke(target, Arrays.copyOf(args, args.length)) :
                                        method.invoke(target));
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
                return null;
            } else {
                /* We should never get to this point. */
                throw new AssertionError("Cannot handle methods from " + method.getDeclaringClass());
            }
        }

        public final void release() {
            this.target = null;
            this.behaviour = null;
        }

        @NotNull
        public final Timer getTimer() {
            return this.timer;
        }
    }

    private static final class ProxyPolicy {

        @NotNull
        private final Object proxy;
        @NotNull
        private final ProxyPolicyInvocationHandler handler;

        public ProxyPolicy(@NotNull Object proxy, @NotNull ProxyPolicyInvocationHandler handler) {
            if (proxy == null) {
                throw new IllegalArgumentException("proxy is null");
            }

            this.proxy = proxy;
            this.handler = handler;
        }

        @NotNull
        public final Object getProxy() {
            return this.proxy;
        }

        @NotNull
        public final ProxyPolicyInvocationHandler getHandler() {
            return this.handler;
        }
    }
}
