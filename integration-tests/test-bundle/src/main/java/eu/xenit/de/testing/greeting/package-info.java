/**
 * The purpose of this package is to trigger Spring's proxy mechanisms using an {@link
 * org.springframework.context.annotation.Configuration} annotated class. By doing so, we try to proactively reproduce
 * any {@link java.lang.ClassNotFoundException}s for Spring's CGLIB classes.
 * <p>
 * Related issue: #306
 */
package eu.xenit.de.testing.greeting;