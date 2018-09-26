package com.github.dynamicextensionsalfresco.policy;

import org.alfresco.repo.policy.Behaviour;

class NoOpBehaviour implements Behaviour {

	/* State */

	private boolean enabled = true;

	private final NotificationFrequency notificationFrequency;

	/* Operations */

	NoOpBehaviour(final NotificationFrequency notificationFrequency, final boolean enabled) {
		this.notificationFrequency = notificationFrequency;
		this.enabled = enabled;
	}

	@Override
	public <T> T getInterface(final Class<T> policy) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disable() {
		enabled = false;
	}

	@Override
	public void enable() {
		enabled = true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public NotificationFrequency getNotificationFrequency() {
		return notificationFrequency;
	}

}
