package eu.xenit.de.testing.exceptionhandlers;

import com.github.dynamicextensionsalfresco.webscripts.annotations.ExceptionHandler;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.http.HttpStatus;

public interface ExceptionHandlers {

    @ExceptionHandler(IAmATeapotException.class)
    default void handle(IAmATeapotException exception, WebScriptResponse response) {
        response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
    }

}
