package com.github.dynamicextensionsalfresco.gradle.internal.rest;

public class RestClientException extends RuntimeException {
    public static class RestClientStatus {
        private final int code;
        private final String message;

        public RestClientStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    private final RestClientStatus status;

    public RestClientException(
            RestClientStatus status, String message) {
        super(message);
        this.status = status;
    }

    public RestClientStatus getStatus() {
        return status;
    }
}
