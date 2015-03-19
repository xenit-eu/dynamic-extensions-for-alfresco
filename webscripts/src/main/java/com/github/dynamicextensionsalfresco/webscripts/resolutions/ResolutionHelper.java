package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import org.alfresco.repo.security.authentication.AuthenticationUtil;

public class ResolutionHelper {
	public static Resolution runAsAdmin(final AbstractResolution resolution) {
		return new AbstractResolution() {
			@Override
			public void resolve() throws Exception {
				AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
					@Override
					public Object doWork() throws Exception {
						resolution.resolve();
						return null;
					}
				});
			}
		};
	}
}
