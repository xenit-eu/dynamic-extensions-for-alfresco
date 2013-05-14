package nl.runnable.alfresco.osgi.spring;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} for creating {@link Mockito} mocks.
 * <p>
 * Note: 'annotation-handling' project also contains a version of this class. Most likely 'annotation-handling' code
 * will be migrated to this project.
 * 
 * @author Laurens Fridael
 * 
 */
public class MockFactoryBean implements FactoryBean<Object> {

	/* Configuration */

	private Class<?> clazz;

	/* State */

	private Object mock;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public Class<? extends Object> getObjectType() {
		return clazz;
	}

	@Override
	public Object getObject() throws Exception {
		if (mock == null) {
			mock = Mockito.mock(clazz);
		}
		return mock;
	}

	/* Configuration */

	public void setClass(final Class<?> clazz) {
		this.clazz = clazz;
	}

}
