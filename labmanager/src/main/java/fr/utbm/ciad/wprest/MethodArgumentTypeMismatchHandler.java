package fr.utbm.ciad.wprest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * A global exception handler for handling
 * {@link MethodArgumentTypeMismatchException} in a Spring MVC application.
 * This class is annotated with {@link ControllerAdvice} to allow it
 * to handle exceptions across all controllers in the application.
 */
@ControllerAdvice
public class MethodArgumentTypeMismatchHandler {

    /**
     * Handles {@link MethodArgumentTypeMismatchException} thrown
     * when a method argument is not convertible to the expected type.
     *
     * @param ex the exception that contains information about the
     *           type mismatch
     * @return a {@link ResponseEntity} containing a detailed error
     *         message and a status of {@link HttpStatus#BAD_REQUEST}
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.format("Invalid value for parameter '%s': '%s'. Expected type: '%s'.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
