# Behaviors / policies

## Creating an annotation based DE Behavior

```java
@Component
@Behaviour
public class SampleBehaviour implements NodeServicePolicies.OnCreateNodePolicy {
    @Override
    public void onCreateNode(ChildAssociationRef childAssocRef) {
        // new node created with reference to parent and child
    }
}
```

Note that we do not need to register our policy implementation. (as is the case for JavaBehaviour)

DE recognizes the implemented interface(s) and registers our instance with the PolicyComponent behind the scenes.

We can limit the triggering of our behaviour by specifying a type on the @Behaviour annotation.
Also we can override when the policy should be triggered by setting the even attribute to:
* FIRST: first time an event occurs
* COMMIT:  triggered from Spring commit listener, before DB commit
* ALL: both

A more complex example can be found at https://github.com/xenit-eu/example-dynamic-extension/blob/master/src/main/java/com/github/dynamicextensionsalfresco/examples/ExampleBehaviour.java.

# Behaviour troubleshooting

## Optional behaviour metrics
    log4j.logger.com.github.dynamicextensionsalfresco.metrics=trace // log DE policy invocation timing 