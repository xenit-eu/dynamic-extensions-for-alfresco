package com.github.dynamicextensionsalfresco.webscripts;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class MessageConverterRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            MessageConverterRegistry.class);

    private final List<HttpMessageConverter<?>> messageConverters;

    private static final boolean jaxb2Present =
            ClassUtils.isPresent("javax.xml.bind.Binder", MessageConverterRegistry.class.getClassLoader());

    private static final boolean jackson2Present =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", MessageConverterRegistry.class.getClassLoader()) &&
                    ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", MessageConverterRegistry.class.getClassLoader());

    private static final boolean jacksonPresent =
            ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", MessageConverterRegistry.class.getClassLoader()) &&
                    ClassUtils.isPresent("org.codehaus.jackson.JsonGenerator", MessageConverterRegistry.class.getClassLoader());



    public MessageConverterRegistry() {
        this.messageConverters = new ArrayList<HttpMessageConverter<?>>();

        if (jackson2Present) {
            // Json 2 (com.fasterxml) is available in the classpath.
            if (ClassUtils.isPresent("org.springframework.http.converter.json.MappingJackson2HttpMessageConverter",
                    MessageConverterRegistry.class.getClassLoader())) {
                // Use the default Spring HttpMessageConverter
                LOGGER.debug("Adding default converter " + MappingJackson2HttpMessageConverter.class.getName());
                this.messageConverters.add(new MappingJackson2HttpMessageConverter());
            } else {
                // No Spring HttpMessageConverter available for Json 2. Use our own implementation.
                LOGGER.debug("Adding default converter " + 
                        com.github.dynamicextensionsalfresco.polyfill.MappingJackson2HttpMessageConverter.class.getName());
                this.messageConverters.add(new com.github.dynamicextensionsalfresco.polyfill.MappingJackson2HttpMessageConverter());
            }
        }
        else if (jacksonPresent) {
            LOGGER.debug("Adding default converter " + MappingJacksonHttpMessageConverter.class.getName());
            this.messageConverters.add(new MappingJacksonHttpMessageConverter());
        }

        if (jaxb2Present) {
            LOGGER.debug("Adding default converter " + Jaxb2RootElementHttpMessageConverter.class.getName());
            this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
    }

    /**
     * Method to register a {@link HttpMessageConverter}. This converter can be used to serialize/deserialize
     * Objects in webscripts. {@code null} values are not allowed.
     * @param messageConverter The message converter that will be added to the register.
     *                         If {@code null} is entered, an {@link IllegalArgumentException} will be thrown.
     *
     * @since 1.7.0
     */
    public void RegisterMessageConvertor(HttpMessageConverter messageConverter) {
        Assert.notNull(messageConverter, "Cannot register 'null' as a messageConverter");
        this.messageConverters.add(messageConverter);
    }

    /**
     * Returns all registered {@link HttpMessageConverter} from this Register class.
     * @return List of messageConverters
     * @since 1.7.0
     */
    public List<HttpMessageConverter<?>> getMessageConverters() {
        return messageConverters;
    }

    /**
     * This method wraps the {@link HttpMessageConverter#canRead(Class, MediaType)} method. It will loop over all
     * registered messageConverters and will call the canRead method on them. If a message converter can read the
     * specified class and mediaType, it will be returned. If no converter can read, {@code null} will be returned.
     * @param clazz the class to test for readability
     * @param mediaType the media type to read, can be {@code null} if not specified.
     * Typically the value of a {@code Content-Type} header.
     * @return The first messageConverter that can read the class and mediaType. If none, value will be null.
     * @since 1.7.0
     */
    public HttpMessageConverter canRead(Class<?> clazz, MediaType mediaType) {
        for (HttpMessageConverter converter : getMessageConverters()) {
            if (converter.canRead(clazz, mediaType)) {
                return converter;
            }
        }

        return null;
    }

    /**
     * This method wraps the {@link HttpMessageConverter#canWrite(Class, MediaType)} method. It will loop over all
     * registered messageConverters and will call the canWrite method on them. The first messageConverter that is able
     * to write the specified class and mediaType will be returned. If no converter can write, {@code null} will be
     * returned.
     * @param clazz the class to test for writability
     * @param mediaType the media type to write, can be {@code null} if not specified.
     * Typically the value of an {@code Accept} header.
     * @return The first messageConverter that can write the class and mediaType. If none, value will be null.
     * @since 1.7.0
     */
    public HttpMessageConverter carWrite(Class<?> clazz, MediaType mediaType) {
        for (HttpMessageConverter converter : getMessageConverters()) {
            if (converter.canWrite(clazz, mediaType)) {
                return converter;
            }
        }

        return null;
    }

    /**
     * Return a List containing all supported {@link MediaType} objects that any registered messageConverter can support.
     * @return List of all supported mediatypes from all registered messageConverters.
     */
    @SuppressWarnings("unchecked")
    public List<MediaType> getSupportedMediaTypes() {
        List<MediaType> supported = new ArrayList<MediaType>();

        for(HttpMessageConverter messageConverter : this.messageConverters){
            supported.addAll(messageConverter.getSupportedMediaTypes());
        }
        return supported;
    }
}
