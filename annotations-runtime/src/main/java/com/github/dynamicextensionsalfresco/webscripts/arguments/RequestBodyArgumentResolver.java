package com.github.dynamicextensionsalfresco.webscripts.arguments;

import com.github.dynamicextensionsalfresco.webscripts.MessageConverterRegistry;
import com.github.dynamicextensionsalfresco.webscripts.messages.AnnotationWebScriptInputMessage;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

public class RequestBodyArgumentResolver implements ArgumentResolver<Object, RequestBody> {


    private MessageConverterRegistry messageConverterRegistry;

    public RequestBodyArgumentResolver(MessageConverterRegistry messageConverterRegistry) {
        this.messageConverterRegistry = messageConverterRegistry;
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

        HttpMessageConverter messageConverter = this.messageConverterRegistry.canRead(argumentType, contentType);

        if (messageConverter == null){
            throw new RuntimeException("Unable to find Convertor for " + contentType.toString());
        }


        // Because the required parameter is not available in the spring version that ships with Alfresco 4.2,
        // we will assume that it is always true.
        // TODO reinsert check for required parameter when alfresco 4.2 is deprecated.

        //if (request.getContent() == null && parameterAnnotation.required()) {
        if (request.getContent() == null) {
            throw new RuntimeException("The content of the request is empty while it is required.");
        }
        //else if (request.getContent() == null && !parameterAnnotation.required()) {
//            return null;
//        }

        boolean isEmpty = this.isBodyEmpty(request.getContent().getInputStream());
        //if (parameterAnnotation.required() && isEmpty) {
        if (isEmpty) {
           throw new RuntimeException("The body of the request is empty while it is required.");
        }
//        else {
//            if (isEmpty) {
//                return null;
//            }
//        }

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
