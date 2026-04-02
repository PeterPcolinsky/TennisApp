package sk.peter.tenis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 *
 * <p>Handles validation errors and illegal arguments,
 * returning structured error responses to the client.</p>
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handles validation errors from @Valid annotated requests.
     *
     * @param ex validation exception
     * @return map of field -> error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> onValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return errors;
    }

    /**
     * Handles illegal argument exceptions.
     *
     * @param ex exception
     * @return error message map
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> onIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();

        errors.put("error", ex.getMessage() != null ? ex.getMessage() : "Bad request");

        return errors;
    }
}