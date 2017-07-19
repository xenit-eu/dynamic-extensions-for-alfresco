package com.github.dynamicextensionsalfresco.policy;

import java.util.ArrayList;

import com.github.dynamicextensionsalfresco.metrics.Timer;
import org.alfresco.repo.policy.Behaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
/**
 * Created by jasper on 19/07/17.
 */
public class DefaultBehaviourProxyFactory  implements BehaviourProxyFactory, ApplicationListener<ContextClosedEvent> {
    private Logger logger = LoggerFactory.getLogger(DefaultBehaviourProxyFactory.class);

    @Autowired
    protected Timer timer;

    private ArrayList<BehaviourProxy> behaviourProxies = new ArrayList<BehaviourProxy>();

    @Override
    public BehaviourProxy createBehaviourProxy(Behaviour behaviour) {
        logger.debug("Creating BehaviourProxy for "+behaviour.getClass().getName()+"${} instance.");
        BehaviourProxy behaviourProxy = new BehaviourProxy(behaviour, timer);
        behaviourProxies.add(behaviourProxy);
        return behaviourProxy;
    }

    /* Callback operations */

    public void  onApplicationEvent( ContextClosedEvent event) {
        releaseBehaviourReferences();
    }

    /* Utility operations */

    protected void releaseBehaviourReferences() {
        for (BehaviourProxy behaviourProxy: behaviourProxies) {
            logger.debug("Releasing reference from BehaviourProxy to "+behaviourProxy.toString()+" instance." );
            behaviourProxy.release();
        }
        behaviourProxies.clear();
    }
}
