package nl.runnable.alfresco.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Description.FormatStyle;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;

class MockWebScriptRequest implements WebScriptRequest {

	private final Map<String, String[]> parametersByName = new HashMap<String, String[]>();

	private final Map<String, String[]> headersByName = new HashMap<String, String[]>();

	private Match serviceMatch;

	/* Main operations */

	public MockWebScriptRequest param(final String name, final String value) {
		parametersByName.put(name, new String[] { value });
		return this;
	}

	public MockWebScriptRequest params(final String name, final String... values) {
		parametersByName.put(name, values);
		return this;
	}

	public MockWebScriptRequest header(final String name, final String value) {
		headersByName.put(name, new String[] { value });
		return this;
	}

	public MockWebScriptRequest headers(final String name, final String... values) {
		headersByName.put(name, values);
		return this;
	}

	/* Simulated operations */

	@Override
	public String[] getParameterNames() {
		return parametersByName.keySet().toArray(new String[parametersByName.size()]);
	}

	@Override
	public String getParameter(final String name) {
		if (parametersByName.containsKey(name)) {
			return parametersByName.get(name)[0];
		} else {
			return null;
		}
	}

	@Override
	public String[] getParameterValues(final String name) {
		return parametersByName.get(name);
	}

	@Override
	public String[] getHeaderNames() {
		return headersByName.keySet().toArray(new String[headersByName.size()]);
	}

	@Override
	public String getHeader(final String name) {
		if (headersByName.containsKey(name)) {
			return headersByName.get(name)[0];
		} else {
			return null;
		}
	}

	@Override
	public String[] getHeaderValues(final String name) {
		return headersByName.get(name);
	}

	public MockWebScriptRequest setServiceMatch(final Match match) {
		this.serviceMatch = match;
		return this;
	}

	@Override
	public Match getServiceMatch() {
		return serviceMatch;
	}

	/* Remaining operations */

	@Override
	public String getServerPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServicePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtensionPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Content getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGuest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormatStyle getFormatStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJSONCallback() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean forceSuccessStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Runtime getRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

}
