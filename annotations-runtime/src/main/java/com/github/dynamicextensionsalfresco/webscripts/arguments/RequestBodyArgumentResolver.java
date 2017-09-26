package com.github.dynamicextensionsalfresco.webscripts.arguments;

import com.github.dynamicextensionsalfresco.webscripts.messages.AnnotationWebScriptInputMessage;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class RequestBodyArgumentResolver implements ArgumentResolver<Object, RequestBody> {


    private final List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

    public RequestBodyArgumentResolver() {
        this.messageConverters.add(new MappingJackson2HttpMessageConverter());
        this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
    }

    @Override
    public boolean supports(Class<?> argumentType, Class<? extends Annotation> annotationType) {
        if (RequestBody.class.equals(annotationType)) {
            return true;
        }

        return false;
    }

    @Override
    public Object resolveArgument(Class<?> argumentType, RequestBody parameterAnnotation, String name, WebScriptRequest request, WebScriptResponse response) {

        String[] headerResponseTypes = request.getHeaderValues("Content-Type");
        if (headerResponseTypes == null) {
            // header does not exist
            throw new RuntimeException("Missing 'Content-Type' header from request. Unable to resolve parameters.");
        }

        if (headerResponseTypes.length != 1) {
            if (headerResponseTypes.length == 0) {
                throw new RuntimeException("Missing 'Content-Type' value from request. Unable to resolve parameters.");
            } else {
                throw new RuntimeException("Multiple 'Content-Type' values found in request. Unable to resolve parameters.");
            }
        }

        MediaType contentType = MediaType.parseMediaType(headerResponseTypes[0]);

        HttpMessageConverter messageConverter = null;
        for (HttpMessageConverter converter : this.messageConverters){
            if (converter.canRead(argumentType, contentType)){
                messageConverter = converter;
                break;
            }
        }

        if (messageConverter == null){
            throw new RuntimeException("Unable to find Convertor for " + contentType.toString());
        }


        if (request.getContent() == null && parameterAnnotation.required()) {
            throw new RuntimeException("The content of the request is empty while it is required.");
        }
        else if (request.getContent() == null && !parameterAnnotation.required()) {
            return null;
        }

        boolean isEmpty = this.isBodyEmpty(request.getContent().getInputStream());
        if (parameterAnnotation.required() && isEmpty) {
           throw new RuntimeException("The body of the request is empty while it is required.");
        }
        else {
            if (isEmpty) {
                return null;
            }
        }

        AnnotationWebScriptInputMessage inputMessage = new AnnotationWebScriptInputMessage(request);

        try {
            Object result = messageConverter.read(argumentType, inputMessage);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    private boolean isBodyEmpty(InputStream inputStream){
        try {
            if (inputStream.available() == 0) {
                return true;
            }

            return false;

        } catch (IOException e) {
            throw new RuntimeException("Error while checking of body is empty", e);
        }


    }

}
