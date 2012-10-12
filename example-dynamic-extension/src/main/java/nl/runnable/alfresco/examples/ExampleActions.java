package nl.runnable.alfresco.examples;

import java.util.Collection;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import nl.runnable.alfresco.actions.annotations.ActionMethod;
import nl.runnable.alfresco.actions.annotations.ActionParam;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * This class demonstrates annotation-based Actions.
 * <p>
 * As with annotated-based WebScripts and Behaviours, you need to annotate the class with {@link ManagedBean} for Spring
 * to instantiate it automatically.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
public class ExampleActions {

	/**
	 * It is recommended practice to declare constants for action and parameter names. These constants should, of
	 * course, be visible to code that uses the action.
	 */
	public static final String SET_DESCRIPTION_ACTION = "Examples_setDescription";

	public static final String DESCRIPTION_PARAM = "description";

	/**
	 * Dependency injection is explained in the {@link CategoriesWebScript} JavaDoc.
	 */
	@Inject
	private NodeService nodeService;

	/**
	 * Methods annotated with {@link ActionMethod} are automatically translated to annotation-based Actions.
	 * <p>
	 * A {@link NodeRef} parameter (without an {@link ActionParam} annotation) is mapped to the {@link NodeRef} that is
	 * being actioned upon. There can only be one parameter of this kind.
	 * <p>
	 * A parameter of type {@link Action} is mapped to the Action instance. There can only be one parameter of this kind
	 * as well.
	 * <p>
	 * Parameters annotated with {@link ActionParam} are mapped to action parameters. If {@link ActionParam#type()} is
	 * not specified, the matching {@link DataTypeDefinition} is determined using the parameter's Java type. For
	 * example: a parameter of type String is mapped to the data type <code>d:text</code>.
	 * <p>
	 * {@link Collection}s parameters are considered to be multi-valued, with the generic type being used for obtaining
	 * a matching {@link DataTypeDefinition}, in case {@link ActionParam#type()} is not specified. For example: a
	 * <code>Collection&ltInteger&gt;</code> is mapped to a multi-valued parameter of type <code>d:int</code>.
	 * 
	 * @param nodeRef
	 *            The {@link NodeRef} being actioned upon.
	 * @param description
	 *            The value of the {@link #DESCRIPTION_PARAM} parameter.
	 */
	@ActionMethod(SET_DESCRIPTION_ACTION)
	public void setDescription(final NodeRef nodeRef, @ActionParam(DESCRIPTION_PARAM) final String description) {
		if (nodeService.exists(nodeRef)) {
			nodeService.setProperty(nodeRef, ContentModel.PROP_DESCRIPTION, description);
		}
	}

}
