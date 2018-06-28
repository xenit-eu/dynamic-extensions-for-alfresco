package com.github.dynamicextensionsalfresco.webscripts;

import com.github.dynamicextensionsalfresco.spring.Spied;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Command;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;

import org.springframework.stereotype.Component;

@Component
@Spied
public class CommandHandler {

	@Uri("/handleCommand")
	public void handleCommand(@Command final Person command) {
	}
}
