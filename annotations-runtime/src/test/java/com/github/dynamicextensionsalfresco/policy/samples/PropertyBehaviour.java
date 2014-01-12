package com.github.dynamicextensionsalfresco.policy.samples;

import com.github.dynamicextensionsalfresco.behaviours.annotations.Behaviour;
import com.github.dynamicextensionsalfresco.behaviours.annotations.PropertyPolicy;

import java.io.Serializable;

/**
 * Sample policy that is bound to cm:name only.
 *
 * @author Laurent Van der Linden
 */
@Behaviour("cm:content")
public class PropertyBehaviour implements DummyPropertyPolicy {
	@PropertyPolicy(property = "cm:name")
	@Override
	public void onNewValue(Serializable newValue) {}
}
