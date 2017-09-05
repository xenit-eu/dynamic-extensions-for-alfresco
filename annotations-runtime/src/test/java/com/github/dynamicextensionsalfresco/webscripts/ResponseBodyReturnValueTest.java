package com.github.dynamicextensionsalfresco.webscripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ResponseBodyReturnValueTest extends AbstractWebScriptAnnotationsTest {

    /**
     * If there is no {@link org.springframework.web.bind.annotation.RequestBody} annotation present,
     * Nothing should happen.
     */
    @Test
    public void testResponseBodyAnnotationNotPresent() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleNoResponseBody",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                new MockWebScriptResponse().setOutputStream(stream));

        assertThat("Webscript response should be empty", stream.toByteArray().length, is(0));
    }

    @Test
    public void testHandleResponseAcceptJsonHeader() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleResponse",
                  new MockWebScriptRequest()
                          .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                          .param("firstName", "Test")
                          .param("lastName", "User"),
                  new MockWebScriptResponse().setOutputStream(stream));

        assertThat("Webscript response should not be empty", stream.toByteArray().length, not(0));

        System.out.println(new String(stream.toByteArray()));

        ObjectMapper mapper = new ObjectMapper();

        Person result = mapper.readValue(stream.toByteArray(), Person.class);

        assertThat("Response of webscript cannot be null.",result, is(not(nullValue())));
        assertThat("FirstName should be 'Test'", result.getFirstName(), is("Test"));
        assertThat("LastName should be 'User'", result.getLastName(), is("User"));
    }

    /**
     * If no Accept Header is send, the webscript default should be used.
     */
    @Test
    public void testHandleDefaultResponse() throws IOException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleDefaultResponse",
                new MockWebScriptRequest()
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                new MockWebScriptResponse().setOutputStream(stream));

        assertThat("Webscript response should not be empty", stream.toByteArray().length, not(0));

        System.out.println(new String(stream.toByteArray()));

        ObjectMapper mapper = new ObjectMapper();

        Person result = mapper.readValue(stream.toByteArray(), Person.class);

        assertThat("Response of webscript cannot be null.",result, is(not(nullValue())));
        assertThat("FirstName should be 'Test'", result.getFirstName(), is("Test"));
        assertThat("LastName should be 'User'", result.getLastName(), is("User"));
    }

    @Test
    public void testHandleResponseAcceptXmlHeader() throws IOException, JAXBException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleXmlResponse",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_XML_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                new MockWebScriptResponse().setOutputStream(stream));
        assertThat("Webscript response should not be empty", stream.toByteArray().length, not(0));
        System.out.println(new String(stream.toByteArray()));

        JAXBContext jaxbContext = JAXBContext.newInstance(PersonXml.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        PersonXml result = (PersonXml) jaxbUnmarshaller.unmarshal(new ByteArrayInputStream(stream.toByteArray()));

        assertThat("Response of webscript cannot be null.", result, is(not(nullValue())));
        assertThat("FirstName should be 'Test'", result.getFirstName(), is("Test"));
        assertThat("LastName should be 'User'", result.getLastName(), is("User"));
    }

    /**
     * The person handleResponse Webscript creates a Person object that cannot be marshalled by jaxb.
     * It should throw an error in this case.
     */
    @Test(expected = RuntimeException.class)
    public void testResponseXmlException() throws IOException, JAXBException {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleResponse",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_XML_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                new MockWebScriptResponse().setOutputStream(stream));

        // request should not return anything
    }

    /**
     * When the media type is unknown, an Runtime exception should be thrown.
     */
    @Test(expected = RuntimeException.class)
    public void testHandleResponseUnknownMediaTypeHeader() {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        handleGet("/handleResponse",
                new MockWebScriptRequest()
                        .header("Accept", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .param("firstName", "Test")
                        .param("lastName", "User"),
                new MockWebScriptResponse().setOutputStream(stream));

        // request should not return anything
    }



}
