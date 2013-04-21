package nl.runnable.alfresco.osgi.webscripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;

/**
 * {@link Path} implementation for compositions of multiple Paths.
 * 
 * @author Laurens Fridael
 * @see CompositeRegistry
 */
public class CompositePath implements Path {

	private final List<Path> paths = new ArrayList<Path>();

	public void addPath(final Path path) {
		Assert.notNull(path, "Path cannot be null.");
		paths.add(path);
	}

	public void removePath(final Path path) {
		Assert.notNull(path, "Path cannot be null.");
		paths.remove(path);
	}

	@Override
	public String getPath() {
		String result = "";
		for (final Path path : paths) {
			result = path.getPath();
			if (result != null) {
				break;
			}
		}
		return result;
	}

	@Override
	public String getName() {
		String name = "";
		for (final Path path : paths) {
			name = path.getName();
			if (name != null) {
				break;
			}
		}
		return name;
	}

	@Override
	public Path getParent() {
		Path parent = null;
		for (final Path path : paths) {
			parent = path.getParent();
			if (parent != null) {
				break;
			}
		}
		return parent;
	}

	@Override
	public Path[] getChildren() {
		final List<Path> allChildren = new ArrayList<Path>();
		for (final Path path : paths) {
			final Path[] children = path.getChildren();
			if (children != null) {
				allChildren.addAll(Arrays.asList(children));
			}
		}
		return allChildren.toArray(new Path[allChildren.size()]);
	}

	@Override
	public WebScript[] getScripts() {
		final List<WebScript> allScripts = new ArrayList<WebScript>();
		for (final Path path : paths) {
			final WebScript[] scripts = path.getScripts();
			if (scripts != null && scripts.length > 0) {
				allScripts.addAll(Arrays.asList(scripts));
			}
		}
		return allScripts.toArray(new WebScript[allScripts.size()]);
	}

}
