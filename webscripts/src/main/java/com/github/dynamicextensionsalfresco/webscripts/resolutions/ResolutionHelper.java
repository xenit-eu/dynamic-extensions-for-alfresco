package com.github.dynamicextensionsalfresco.webscripts.resolutions;

import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebScriptRequest;
import com.github.dynamicextensionsalfresco.webscripts.AnnotationWebscriptResponse;
import org.alfresco.repo.security.authentication.AuthenticationUtil;

public class ResolutionHelper {
	public static Resolution runAsSystem(final Resolution resolution) {
		return new Resolution() {
			@Override
			public void resolve(final AnnotationWebScriptRequest request, final AnnotationWebscriptResponse response, final ResolutionParameters params) throws Exception {
				AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<Object>() {
					@Override
					public Object doWork() throws Exception {
						resolution.resolve(request, response, params);
						return null;
					}
				});
			}
		};
	}
}
