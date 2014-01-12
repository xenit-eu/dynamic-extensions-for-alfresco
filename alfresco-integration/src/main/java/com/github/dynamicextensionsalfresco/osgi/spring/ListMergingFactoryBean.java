package com.github.dynamicextensionsalfresco.osgi.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} for merging multiple {@link List}s into a single List.
 * 
 * @author Laurens Fridael
 * @see SystemPackageConfigurationFactoryBean
 * @param T
 */
public class ListMergingFactoryBean<T> implements FactoryBean<List<T>> {

	/* Configuration */

	private List<List<T>> lists;

	/* Operations */

	@Override
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends List<T>> getObjectType() {
		return (Class<? extends List<T>>) (Class<?>) List.class;
	}

	@Override
	public List<T> getObject() {
		final List<T> lists = new ArrayList<T>();
		for (final List<T> list : getLists()) {
			lists.addAll(list);
		}
		return lists;
	}

	/* Configuration */

	public void setLists(final List<List<T>> systemPackages) {
		this.lists = systemPackages;
	}

	protected List<List<T>> getLists() {
		return lists;
	}

}
