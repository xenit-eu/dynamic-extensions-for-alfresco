/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dynamicextensionsalfresco.polyfill;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

/**
 * Copy of the Spring implementation to support Json 2 (com.fasterxml) in Alfresco 4.2
 *
 * In Alfresco 4.2, the default Spring MappingJackson2HttpMessageConverter to support Json 2 (com.fasterxml) is not yet
 * available. Nevertheless, we want to support Json 2 if the {@link ObjectMapper} is available on the classpath.
 */
public class MappingJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object>
        implements HttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");


    private ObjectMapper objectMapper = new ObjectMapper();

    private String jsonPrefix;

    private Boolean prettyPrint;


    public MappingJackson2HttpMessageConverter() {
        super(new MediaType("application", "json", DEFAULT_CHARSET),
                new MediaType("application", "*+json", DEFAULT_CHARSET));
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
        }
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? "{} && " : null);
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        configurePrettyPrint();
    }


    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return canRead(clazz, null, mediaType);
    }

    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        JavaType javaType = getJavaType(type, contextClass);
        return (this.objectMapper.canDeserialize(javaType) && canRead(mediaType));
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return (this.objectMapper.canSerialize(clazz) && canWrite(mediaType));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {

        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }


    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        JsonGenerator jsonGenerator =
                this.objectMapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);

        // A workaround for JsonGenerators not applying serialization features
        // https://github.com/FasterXML/jackson-databind/issues/12
        if (this.objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jsonGenerator.useDefaultPrettyPrinter();
        }

        try {
            if (this.jsonPrefix != null) {
                jsonGenerator.writeRaw(this.jsonPrefix);
            }
            this.objectMapper.writeValue(jsonGenerator, object);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    protected JavaType getJavaType(Type type, Class<?> contextClass) {
        return (contextClass != null) ?
                this.objectMapper.getTypeFactory().constructType(type, contextClass) :
                this.objectMapper.constructType(type);
    }

    protected JsonEncoding getJsonEncoding(MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            Charset charset = contentType.getCharSet();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }

}
