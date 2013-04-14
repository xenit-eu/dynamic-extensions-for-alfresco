package nl.runnable.alfresco.osgi.spring;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * {@link FactoryBean} that combines all the items from multiple Sets into a single Set.
 * <p>
 * This implementation preserves the original item order.
 * 
 * @author Laurens Fridael
 * 
 * @param <T>
 */
public class SetCombinationFactoryBean<T> implements FactoryBean<Set<T>> {

	/* Configuration */

	private List<Set<T>> sets;

	/* Main operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Set<T>> getObjectType() {
		return (Class<? extends Set<T>>) (Class<?>) Set.class;
	}

	@Override
	public Set<T> getObject() throws Exception {
		final Set<T> allItems = new LinkedHashSet<T>(getTotalSize());
		for (final Set<T> set : getSets()) {
			allItems.addAll(set);
		}
		return allItems;
	}

	/* Utility operations */

	protected int getTotalSize() {
		int totalSize = 0;
		for (final Set<T> set : getSets()) {
			totalSize += set.size();
		}
		return totalSize;
	}

	/* Configuration */

	public void setSets(final List<Set<T>> sets) {
		Assert.notNull(sets);
		this.sets = sets;
	}

	protected List<Set<T>> getSets() {
		return sets;
	}
}
