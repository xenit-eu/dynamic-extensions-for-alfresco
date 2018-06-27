package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Header;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;

import org.springframework.stereotype.Component;

@Component
@Spied
public class HeaderHandler {

	@Uri("/handleHeader")
	public void handleHeader(@Header("Content-Type") final String contentType) {
	}
}
