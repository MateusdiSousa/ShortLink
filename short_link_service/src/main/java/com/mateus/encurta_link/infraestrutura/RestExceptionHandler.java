package com.mateus.encurta_link.infraestrutura;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mateus.encurta_link.exceptions.InvalidCredentialsException;
import com.mateus.encurta_link.exceptions.ShortLinkConflictException;
import com.mateus.encurta_link.exceptions.ShortLinkNotFoundException;
import com.mateus.encurta_link.exceptions.UserAlreadyExistException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ShortLinkNotFoundException.class)
    private ResponseEntity<String> shortLinkNotFoundHandler(ShortLinkNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short link not found.");
    }

    @ExceptionHandler(value = ShortLinkConflictException.class)
    private ResponseEntity<String> shortLinkConflictHandler(ShortLinkConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Short link already exist.");
    }

    @ExceptionHandler(value = InvalidCredentialsException.class)
    private ResponseEntity<String> invalidCredentialHandler(InvalidCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password invalid.");
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    private ResponseEntity<String> userAlreadyExistHandler(UserAlreadyExistException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
