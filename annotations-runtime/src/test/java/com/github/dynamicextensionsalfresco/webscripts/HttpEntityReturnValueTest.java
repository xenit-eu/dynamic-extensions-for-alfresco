package com.github.dynamicextensionsalfresco.webscripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.github.dynamicextensionsalfresco.webscripts.HttpEntityReturnValueHandler.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class HttpEntityReturnValueTest extends AbstractWebScriptAnnotationsTest {

    @Test
    public void testHttpEntityResponseWithHeaders() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MockWebScriptResponse response = new MockWebScriptResponse().setOutputStream(stream);

        handleGet("/handleHttpEntityResponseWithHeaders",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                response);

        assertThat("Webscript response should be empty", stream.toByteArray().length, is(0));

        Map<String, List<String>> headers = response.getHeaders();
        List<String> testHeader1 = headers.get(HEADER_TEST_KEY);
        assertTrue("Webscript response should contain test header", (testHeader1 != null && !testHeader1.isEmpty()));
        assertThat("Webscript response test header should contain 2 values", testHeader1.size(), is(2));
        assertTrue("Webscript response test header should contain proper value", testHeader1.contains(HEADER_TEST_VALUE_1));
        assertTrue("Webscript response test header should contain proper value", testHeader1.contains(HEADER_TEST_VALUE_2));

        List<String> testHeader2 = headers.get(HEADER_TEST_KEY_BIS);
        assertTrue("Webscript response should contain the second test header", (testHeader2 != null && !testHeader2.isEmpty()));
        assertThat("Webscript response: second test header should contain 2 values", testHeader2.size(), is(1));
        assertTrue("Webscript response: second test header should contain proper value", testHeader2.contains(HEADER_TEST_VALUE_BIS));
    }

    @Test
    public void testResponseEntityResponseWithStatusCode() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MockWebScriptResponse response = new MockWebScriptResponse().setOutputStream(stream);

        handleGet("/handleResponseEntityResponseWithStatusCode",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                response);

        assertThat("Webscript response should be empty", stream.toByteArray().length, is(0));

        assertThat("Webscript response status code should be correct", response.getStatus(), is(HttpStatus.I_AM_A_TEAPOT.value()));

    }

    @Test
    public void testHandleResponseWithBodyAcceptJsonHeader() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleHttpEntityResponseWithBody",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                new MockWebScriptResponse().setOutputStream(stream));

        assertThat("Webscript response should not be empty", stream.toByteArray().length, not(0));

        ObjectMapper mapper = new ObjectMapper();

        Person result = mapper.readValue(stream.toByteArray(), Person.class);

        assertThat("Response of webscript cannot be null.", result, is(not(nullValue())));
        assertThat("FirstName should be 'Test'", result.getFirstName(), is("Test"));
        assertThat("LastName should be 'User'", result.getLastName(), is("User"));
    }

    @Test
    public void testHandleResponseWithBodyAcceptXmlHeader() throws IOException, JAXBException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleHttpEntityResponseWithXmlBody",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_XML_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                new MockWebScriptResponse().setOutputStream(stream));
        assertThat("Webscript response should not be empty", stream.toByteArray().length, not(0));

        JAXBContext jaxbContext = JAXBContext.newInstance(PersonXml.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        PersonXml result = (PersonXml) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(stream.toByteArray()));

        assertThat("Response of webscript cannot be null.", result, is(not(nullValue())));
        assertThat("FirstName should be 'Test'", result.getFirstName(), is("Test"));
        assertThat("LastName should be 'User'", result.getLastName(), is("User"));
    }
}
