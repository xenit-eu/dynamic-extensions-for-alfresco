package eu.xenit.de.testing.actions;

import com.github.dynamicextensionsalfresco.actions.annotations.ActionMethod;
import com.github.dynamicextensionsalfresco.actions.annotations.ActionParam;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.stereotype.Component;

@Component
public class TestAction {

    @ActionMethod(value = "testAction")
    public void testAction(final NodeRef nodeRef, @ActionParam("name") final String name) {
        throw new UnsupportedOperationException();
    }

}
