package com.github.dynamicextensionsalfresco.event.events

import com.github.dynamicextensionsalfresco.event.Event
import org.osgi.framework.Bundle

/**
 * @author Laurent Van der Linden
 */
public class SpringContextException(public val bundle: Bundle, public val exception: Exception) : Event
