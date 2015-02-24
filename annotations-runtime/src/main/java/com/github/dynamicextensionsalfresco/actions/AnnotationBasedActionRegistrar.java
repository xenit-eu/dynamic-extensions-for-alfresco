package com.github.dynamicextensionsalfresco.actions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.dynamicextensionsalfresco.AbstractAnnotationBasedRegistrar;
import com.github.dynamicextensionsalfresco.actions.annotations.ActionMethod;
import com.github.dynamicextensionsalfresco.actions.annotations.ActionParam;

import com.google.common.collect.ImmutableMap;
import org.alfresco.repo.action.ActionDefinitionImpl;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.RuntimeActionService;
import org.alfresco.repo.action.executer.ActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;

/**
 * Manages annotation-based Actions in a {@link BeanFactory}.
 * <p>
 * This implementation scans beans for methods annotated with {@link ActionMethod}, creates corresponding parameter
 * mappings and registers and unregisters the resulting {@link ActionExecuter} with an {@link ActionExecuterRegistry}.
 * 
 * @author Laurens Fridael
 * 
 */
public class AnnotationBasedActionRegistrar extends AbstractAnnotationBasedRegistrar {

	/* Dependencies */

	private DictionaryService dictionaryService;

	private ActionExecuterRegistry actionExecuterRegistry;

	private RuntimeActionService runtimeActionService;

	private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	private final Map<Class<?>,QName> parameterTypes = getActionParameterMapping();

	/* State */

	private final List<ActionExecuter> registeredActionExecutors = new ArrayList<ActionExecuter>();

	/* Operations */

	public void registerAnnotationBasedActions() {
		final ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		for (final String beanName : beanFactory.getBeanDefinitionNames()) {
			final Class<?> type = beanFactory.getType(beanName);
			if (type != null) { // bean might be abstract
				ReflectionUtils.doWithMethods(type, new MethodCallback() {

					@Override
					public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
						final ActionMethod actionMethod = AnnotationUtils.findAnnotation(method, ActionMethod.class);
						if (actionMethod != null) {
							final ActionExecuter actionExecuter = createActionExecuter(beanFactory.getBean(beanName),
								method, actionMethod);
							final ActionDefinition actionDefinition = actionExecuter.getActionDefinition();
							final String name = actionDefinition.getName();
							if (getActionExecuterRegistry().hasActionExecuter(name) == false) {
								if (logger.isDebugEnabled()) {
									logger.debug("Registering ActionExecuter {}.", name);
								}
								getActionExecuterRegistry().registerActionExecuter(actionExecuter);
								registeredActionExecutors.add(actionExecuter);
								registerActionToken(actionDefinition);
							} else {
								if (logger.isWarnEnabled()) {
									logger.warn("ActionExecuter name has already been registered.");
								}
							}
						}
					}
				});
			}
		}
	}

	/**
	 * Registry a dummy action so that the action list is extended. It will not be executed unless the AnnotatedExecutor
	 * is uninstalled.
	 */
	private synchronized void registerActionToken(final ActionDefinition actionDefinition) {
		runtimeActionService.registerActionExecuter(new DummyActionExecutor(actionDefinition));
	}

	public void unregisterAnnotationBasedActions() {
		for (final ActionExecuter actionExecuter : registeredActionExecutors) {
			logger.debug("Unregistering ActionExecuter {}.", actionExecuter.getActionDefinition().getName());
			getActionExecuterRegistry().unregisterActionExecuter(actionExecuter);
		}
	}

	protected ActionExecuter createActionExecuter(final Object bean, final Method method,
			final ActionMethod actionMethod) {
		final List<ParameterDefinition> parameterDefinitions = new ArrayList<ParameterDefinition>();
		final ActionMethodMapping mapping = resolveActionMethodMapping(bean, method, parameterDefinitions);
		return new AnnotationBasedActionExecuter(createActionDefinition(method, actionMethod, parameterDefinitions),
				mapping, nullForEmptyString(actionMethod.queueName()));
	}

	protected ActionDefinition createActionDefinition(final Method method, final ActionMethod actionMethod,
			final List<ParameterDefinition> parameterDefinitions) {
		String name = actionMethod.value();
		if (StringUtils.isEmpty(name)) {
			name = String.format("%s.%s", ClassUtils.getShortName(method.getDeclaringClass()), method.getName());
		}
		final ActionDefinitionImpl actionDefinition = new ActionDefinitionImpl(name);
		actionDefinition.setParameterDefinitions(parameterDefinitions);
		actionDefinition.setAdhocPropertiesAllowed(actionMethod.adhocPropertiesAllowed());
		ApiCompatibilityUtil.setApplicableTypes(actionDefinition,
				Arrays.asList(parseQNames(actionMethod.applicableTypes(), actionMethod)));
		actionDefinition.setTitleKey(nullForEmptyString(actionMethod.titleKey()));
		actionDefinition.setDescriptionKey(nullForEmptyString(actionMethod.descriptionKey()));
		actionDefinition.setRuleActionExecutor(nullForEmptyString(actionMethod.ruleActionExecutor()));
		return actionDefinition;
	}

	protected ActionMethodMapping resolveActionMethodMapping(final Object bean, final Method method,
			final List<ParameterDefinition> parameterDefinitions) {
		final ActionMethodMapping mapping = new ActionMethodMapping(bean, method);
		final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		final Type[] parameterTypes = method.getGenericParameterTypes();
		final String[] methodParameterNames = parameterNameDiscoverer.getParameterNames(method);
		for (int index = 0; index < parameterTypes.length; index++) {
			final Type parameterType = parameterTypes[index];
			Class<?> clazz = null;
			boolean multivalued = false;
			if (parameterType instanceof Class<?>) {
				clazz = (Class<?>) parameterType;
			} else if (isCollectionType(parameterType)) {
				multivalued = true;
				clazz = getCollectionType((ParameterizedType) parameterType);
			}
			if (clazz == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("Cannot handle Parameter type {}.", parameterType);
				}
				continue;
			}
			if (multivalued == false) {
				if (NodeRef.class.isAssignableFrom(clazz)) {
					boolean hasParameterAnnotation = false;
					for (final Annotation parameterAnnotation : parameterAnnotations[index]) {
						if (parameterAnnotation instanceof ActionParam) {
							hasParameterAnnotation = true;
							break;
						}
					}
					if (hasParameterAnnotation == false) {
						if (mapping.getNodeRefParameterIndex() < 0) {
							mapping.setNodeRefParameterIndex(index);
						} else {
							if (logger.isWarnEnabled()) {
								logger.warn("NodeRef parameter has already been set. "
										+ "Duplicate NodeRef parameters without an @ActionParam will be ignored.");
							}
						}
					}
				} else if (Action.class.isAssignableFrom(clazz)) {
					if (mapping.getActionParameterIndex() < 0) {
						mapping.setActionParameterIndex(index);
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("Action parameter has already been configured. Duplicate Action parameters will be ignored.");
						}
					}
				}
			}
			for (final Annotation parameterAnnotation : parameterAnnotations[index]) {
				if (parameterAnnotation instanceof ActionParam) {
					final ActionParam actionParameter = (ActionParam) parameterAnnotation;
					String name = actionParameter.value();
					if (StringUtils.isEmpty(name)) {
						name = methodParameterNames[index];
					}
					if (StringUtils.isEmpty(name)) {
						throw new RuntimeException(
								String.format(
										"Cannot determine name of parameter at index %s of method %s."
												+ " No name specified in @RequestParam annotation and no name available in debug info.",
										index, method));
					}
					final DataTypeDefinition dataType = getDataType(clazz, actionParameter);
					if (dataType == null) {
						throw new RuntimeException(
								String.format("Cannot map parameter of type %s for action method %s."
										+ " Make sure you specify a valid DataType from the Dictionary.", clazz, method));
					}
					final boolean mandatory = actionParameter.mandatory();
					final String displayLabel = actionParameter.displayLabel();
					final String constraintName = StringUtils.stripToNull(actionParameter.constraintName());
					final ParameterDefinition parameterDefinition = new ParameterDefinitionImpl(name,
							dataType.getName(), mandatory, displayLabel, multivalued, constraintName);
					parameterDefinitions.add(parameterDefinition);
					final String parameterName = parameterDefinition.getName();
					if (mapping.hasParameter(parameterName) == false) {
						mapping.addParameterMapping(new ParameterMapping(parameterDefinition, index, clazz));
					} else {
						throw new RuntimeException(String.format("Duplicate parameter '%s'", parameterName));
					}

				}
			}

		}
		return mapping;
	}

	private DataTypeDefinition getDataType(final Class<?> clazz, final ActionParam actionParameter) {
		final DataTypeDefinition dataType;
		if (StringUtils.isNotEmpty(actionParameter.type())) {
			dataType = getDictionaryService().getDataType(parseQName(actionParameter.type(), actionParameter));
			if (dataType == null) {
				throw new RuntimeException(String.format("Invalid or unknown DataType: %s", actionParameter.type()));
			}
		} else {
			final QName qName = parameterTypes.get(clazz);
			if (qName == null) {
				throw new RuntimeException(String.format("%s is not a standard parameter type, specify the type explicitly", clazz));
			}
			dataType = getDictionaryService().getDataType(qName);
		}
		return dataType;
	}

	private static boolean isCollectionType(final Type type) {
		if (type instanceof ParameterizedType) {
			final ParameterizedType parameterizedType = (ParameterizedType) type;
			final Type rawType = parameterizedType.getRawType();
			return (rawType instanceof Class<?> && Collection.class.isAssignableFrom(((Class<?>) rawType)));
		}
		return false;
	}

	private Class<?> getCollectionType(final ParameterizedType parameterizedType) {
		final Type rawType = parameterizedType.getRawType();
		final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		if (rawType instanceof Class<?> && Collection.class.isAssignableFrom(((Class<?>) rawType))
				&& actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class<?>) {
			return (Class<?>) actualTypeArguments[0];
		} else {
			return null;
		}

	}

	protected Map<Class<?>, QName> getActionParameterMapping() {
		return ImmutableMap.<Class<?>,QName>builder()
			.put(String.class, DataTypeDefinition.TEXT)
			.put(Long.class, DataTypeDefinition.LONG)
			.put(Double.class, DataTypeDefinition.DOUBLE)
			.put(Date.class, DataTypeDefinition.DATETIME)
			.put(Boolean.class, DataTypeDefinition.BOOLEAN)
			.put(NodeRef.class, DataTypeDefinition.NODE_REF)
			.put(QName.class, DataTypeDefinition.QNAME)
			.build();
	}

	/* Dependencies */

	public void setDictionaryService(final DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	protected DictionaryService getDictionaryService() {
		return dictionaryService;
	}

	public void setActionExecuterRegistry(final ActionExecuterRegistry actionExecuterRegistry) {
		this.actionExecuterRegistry = actionExecuterRegistry;
	}

	protected ActionExecuterRegistry getActionExecuterRegistry() {
		return actionExecuterRegistry;
	}

	public void setRuntimeActionService(final RuntimeActionService runtimeActionService) {
		this.runtimeActionService = runtimeActionService;
	}
}
