package nl.runnable.alfresco.blueprint;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Constants;

/**
 * Contains utility functions for parsing OSGi manifest headers.
 * 
 * @author Laurens Fridael
 */
class ManifestHeaderParser {

	private static enum ParseState {
		EXPORT_PACKAGE, ATTRIBUTE, ATTRIBUTE_VALUE,
	}

	/**
	 * Obtains the exported packages from the {@link Constants#EXPORT_PACKAGE} header.
	 * 
	 * @param value
	 * @return
	 */
	static String[] parseExportedPackages(final CharSequence value) {
		final List<String> exportedPackages = new ArrayList<String>();
		ParseState parseState = ParseState.EXPORT_PACKAGE;
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			final char ch = value.charAt(i);
			switch (parseState) {
			case EXPORT_PACKAGE:
				switch (ch) {
				case ';':
					parseState = ParseState.ATTRIBUTE;
					exportedPackages.add(sb.toString());
					sb.delete(0, sb.length());
					break;
				case ',':
					exportedPackages.add(sb.toString());
					sb.delete(0, sb.length());
				default:
					sb.append(ch);
					break;
				}
				if (i == value.length() - 1 && sb.length() > 0) {
					exportedPackages.add(sb.toString());
				}
			case ATTRIBUTE:
				switch (ch) {
				case '"':
					parseState = ParseState.ATTRIBUTE_VALUE;
					break;
				case ',':
					parseState = ParseState.EXPORT_PACKAGE;
					break;
				}
				break;
			case ATTRIBUTE_VALUE:
				switch (ch) {
				case '"':
					parseState = ParseState.ATTRIBUTE;
					break;
				}
				break;
			}
		}
		return exportedPackages.toArray(new String[exportedPackages.size()]);
	}

}
