package com.github.dynamicextensionsalfresco.webscripts;

import org.springframework.extensions.surf.util.Content;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class MockWebscriptContent implements Content {


    private String content;
    private String mimeType;
    private String encoding;
    private long size;
    private InputStream inputStream;
    private Reader reader;


    public MockWebscriptContent() {

    }

    public MockWebscriptContent(String content, String mimeType, String encoding, long size, InputStream inputStream, Reader reader) {
        this.content = content;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.size = size;
        this.inputStream = inputStream;
        this.reader = reader;
    }

    @Override
    public String getContent() throws IOException {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getMimetype() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public MockWebscriptContent with(InputStream inputStream) {
        this.setInputStream(inputStream);

        return this;
    }

    @Override
    public Reader getReader() throws IOException {
        return this.reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }
}
