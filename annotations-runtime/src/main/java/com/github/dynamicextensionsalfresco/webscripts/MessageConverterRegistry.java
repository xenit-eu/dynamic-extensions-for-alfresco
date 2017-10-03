package com.github.dynamicextensionsalfresco.webscripts;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageConverterRegistry {
    private final List<HttpMessageConverter<?>> messageConverters;

    public MessageConverterRegistry() {
        this.messageConverters = new ArrayList<HttpMessageConverter<?>>();

        this.messageConverters.add(new MappingJackson2HttpMessageConverter());
        this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
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
