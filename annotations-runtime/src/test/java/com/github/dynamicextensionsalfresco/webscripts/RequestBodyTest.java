package com.github.dynamicextensionsalfresco.webscripts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

public class RequestBodyTest extends AbstractWebScriptAnnotationsTest {

    @Autowired
    private RequestBodyHandler handler;

    /**
     * This needs to be done to prevent mockito from failing.
     * If the reset is not called, it will fail with org.mockito.exceptions.verification.TooManyActualInvocations
     *
     */
    @After
    public void tearDown() {
        Mockito.reset(handler);
    }


    @Test
    public void requiredBodyAcceptXml() throws JsonProcessingException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PersonXml.class);

        PersonXml personXml = new PersonXml("test", "user");

        Marshaller marshaller = jaxbContext.createMarshaller();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        marshaller.marshal(personXml, outputStream);

        String value = new String(outputStream.toByteArray());


        System.out.println(value);

        InputStream inputStream = new ByteArrayInputStream(value.getBytes());

        MockWebScriptRequest request = new MockWebScriptRequest().header("Content-Type", "application/xml");
        request.setContent(new MockWebscriptContent().with(inputStream));

        handlePost("/requestbody/required", request);


        verify(handler).requiredBody(argThat(new ArgumentMatcher<PersonXml>() {
            @Override
            public boolean matches(Object argument) {
                return argument.equals(personXml);
            }
        }));

    }

    @Test
    public void requiredBodyAcceptJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        PersonXml personXml = new PersonXml("test", "user");
        String value = mapper.writeValueAsString(personXml);
        System.out.println(value);

        InputStream inputStream = new ByteArrayInputStream(value.getBytes());

        MockWebScriptRequest request = new MockWebScriptRequest().header("Content-Type", "application/json");
        request.setContent(new MockWebscriptContent().with(inputStream));

        handlePost("/requestbody/required", request);


        verify(handler).requiredBody(argThat(new ArgumentMatcher<PersonXml>() {
            @Override
            public boolean matches(Object argument) {
                return argument.equals(personXml);
            }
        }));
    }

    /**
     * When required = true, a Runtime exception is thrown.
     */
    @Test(expected = RuntimeException.class)
    public void requestBodyInputStreamNullRequiredTrue() {
        MockWebScriptRequest request = new MockWebScriptRequest().header("Content-Type", "application/json");
        handlePost("/requestbody/required", request);
    }

    @Test
    public void requestBodyInputStreamNullRequiredFalse() {
        MockWebScriptRequest request = new MockWebScriptRequest().header("Content-Type", "application/json");
        handlePost("/requestbody/notRequired", request);
        verify(handler).notRequired(argThat(new ArgumentMatcher<PersonXml>() {
            @Override
            public boolean matches(Object argument) {
                return argument == null;
            }
        }));
    }

    @Test(expected = RuntimeException.class)
    public void requestBodyEmptyRequiredTrue() {
        MockWebScriptRequest request = new MockWebScriptRequest().header("Content-Type", "application/json");
        request.setContent(new MockWebscriptContent().with(new ByteArrayInputStream(new byte[0])));
        handlePost("/requestbody/required", request);
    }

    @Test
    public void requestBodyEmptyRequiredFalse() {
        MockWebScriptRequest request = new MockWebScriptRequest().header("Content-Type", "application/json");
        request.setContent(new MockWebscriptContent().with(new ByteArrayInputStream(new byte[0])));
        handlePost("/requestbody/notRequired", request);
        verify(handler).notRequired(argThat(new ArgumentMatcher<PersonXml>() {
            @Override
            public boolean matches(Object argument) {
                return argument == null;
            }
        }));
    }


}
