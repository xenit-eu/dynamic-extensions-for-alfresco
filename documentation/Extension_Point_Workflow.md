# Workflow definitions

Similar to the support for dynamic document models, workflow definition files can be packaged in your extension jar.

They should be placed under `/META-INF/alfresco/workflows/*.bpmn20.xml`.

The framework will put all found definition files in Alfresco's `Data Dictionary / Workflow Definitions` folder if their content does not match any existing file with the same name.

The actual registration of the workflow is handled by an Alfresco policy.

This means that unlike the standard workflow deployer bean, your workflow version will not get incremented on every restart of Alfresco.

# Service tasks

Activiti allows you to reference Java classes to handle automated workflow steps via a ServiceTask. This can either be a classname or a delegate expression.

When a classname is provided, an instance is simply instantited without any link to Spring.

This is where delegate expressions come in. They allow you to reference either a global Spring bean or a component in a DE extension, provided the target component implements the correct interface.

In your bpmn20.xml, define a service class:

```xml
<serviceTask name="DoSomethingDynamic" 
             activiti:delegateExpression="${MyextensionSomeComponent}"/>
```

This will resolve the delegates expression to a component with that id.

This means that you should take care of using a unique componentId. You can do this with the component annotation: `@Component("MyextensionSomeComponent")`.

Here is the complete example:

```java
@Component("MyextensionSomeComponent")
public class DoSomething implements JavaDelegate {
  public void execute(DelegateExecution execution) {}
}
```

# Task listeners

Task listeners can be used to add behaviour to the start or end of a workflow (user)task.

The concept is very similar, in the extensionelements of a task:

```xml
<activiti:taskListener event="complete" activiti:delegateExpression="${MyextensionSomeComponent}"/>
```

```java
@Component("MyextensionSomeComponent")
public class DoSomething implements TaskListener {
  public void notify(DelegateTask delegateTask) {}
}
```

# Execution listener

And finally, listeners to be used for the execution level (workflow process):

```xml
<activiti:executionListener activiti:delegateExpression="${MyextensionSomeComponent}" event="end"/>
```

```java
@Component("MyextensionSomeComponent")
public class DoSomething implements ExecutionListener {
  public void notify(DelegateExecution delegateTask) {}
}
```

# Extension elements

We can easily make reusable TaskListeners (or Delegates) by adding <activiti:field> to their definition.
This essentially allows you to specify parameters.

Take this workflow definition snippet:
```xml
<userTask id="reviewTask" name="Review proposal" activiti:formKey="wf:review">
    <extensionElements>
        <activiti:taskListener event="complete" delegateExpression="${TaskCompleteListener}">
            <activiti:field name="color">
                <activiti:string>purple</activiti:string>
            </activiti:field>
        </activiti:taskListener>
    </extensionElements>
</userTask>
 
```

We can match this in our Java code by adding a `org.activiti.engine.impl.el.FixedValue` field named color. Make sure to add both a private field and a setter.

We can then read the parameter both literally or evaluated in the task or execution content:
```java
public class CompleteListener implements TaskListener {
  private FixedValue color;

  public void setColor(FixedValue color) {
    this.color = color;
  }

  @Override public void notify(DelegateTask delegateTask) {
    String literal = color.getExpressionText();
    Object evaluated = color.getValue(delegateTask);
  }
}
```