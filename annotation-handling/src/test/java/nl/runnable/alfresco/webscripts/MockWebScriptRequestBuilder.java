package nl.runnable.alfresco.webscripts;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.extensions.webscripts.WebScriptRequest;

class MockWebScriptRequestBuilder {

	private final Map<String, Object> parametersByName = new HashMap<String, Object>();

	private final Map<String, Object> headersByName = new HashMap<String, Object>();

	public MockWebScriptRequestBuilder param(final String name, final String value) {
		parametersByName.put(name, value);
		return this;
	}

	public MockWebScriptRequestBuilder params(final String name, final String... values) {
		parametersByName.put(name, values);
		return this;
	}

	public WebScriptRequest build() {
		final WebScriptRequest request = mock(WebScriptRequest.class);
		when(request.getParameter(anyString())).thenAnswer(new Answer<String>() {

			@Override
			public String answer(final InvocationOnMock invocation) throws Throwable {
				return (String) parametersByName.get(invocation.getArguments()[0]);
			}
		});
		when(request.getParameterValues(anyString())).then(new Answer<String[]>() {

			@Override
			public String[] answer(final InvocationOnMock invocation) throws Throwable {
				return (String[]) parametersByName.get(invocation.getArguments()[0]);
			}
		});
		when(request.getHeader(anyString())).thenAnswer(new Answer<String>() {

			@Override
			public String answer(final InvocationOnMock invocation) throws Throwable {
				return (String) headersByName.get(invocation.getArguments()[0]);
			}
		});
		return request;
	}

	public MockWebScriptRequestBuilder header(final String name, final String value) {
		headersByName.put(name, value);
		return this;
	}
}
