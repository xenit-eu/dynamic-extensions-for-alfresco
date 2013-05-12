package nl.runnable.alfresco.osgi;

import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarPackageScanner {

	public Set<String> scanJarForPackages(final JarFile jarFile) {
		final Set<String> packages = new TreeSet<String>();
		for (final Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
			final JarEntry jarEntry = entries.nextElement();
			String name = jarEntry.getName();
			if (name.endsWith(".class")) {
				name = name.substring(0, name.lastIndexOf('/')).replace('/', '.');
				packages.add(name);
			}
		}

		return packages;
	}

	/* Main operations */

}
