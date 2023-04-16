package com.mediscreen.patientmicroservice.exception_handler;

import com.mediscreen.patientmicroservice.exceptions.PatientAlreadyExistException;
import com.mediscreen.patientmicroservice.exceptions.PatientNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A custom(Global) exception handler for handling Patient-related exceptions in the application.
 */
@ControllerAdvice
public class PatientExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     * Handle the PatientNotFoundException.
     *
     * @param patientNotFoundException exception to handle
     * @param webRequest               WebRequest
     * @return a ResponseEntity with an ErrorMessage and HttpStatus.NOT_FOUND
     */
    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ResponseMessage> handlePatientNotFoundException(PatientNotFoundException patientNotFoundException, WebRequest webRequest) {
        ResponseMessage errorResponse = new ResponseMessage(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                patientNotFoundException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle the PatientAlreadyExistException.
     *
     * @param patientAlreadyExistException the exception to handle
     * @param webRequest                   WebRequest
     * @return a ResponseEntity with an ErrorMessage and HttpStatus.BAD_REQUEST
     */
    @ExceptionHandler(PatientAlreadyExistException.class)
    public ResponseEntity<ResponseMessage> handlePatientAlreadyExistException(PatientAlreadyExistException patientAlreadyExistException, WebRequest webRequest) {
        ResponseMessage errorResponse = new ResponseMessage(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                patientAlreadyExistException.getMessage(),
                webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", status.value());

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage) //x -> x.getDefaultMessage()
                .collect(Collectors.toList());

        responseBody.put("errors", errors);

        return new ResponseEntity<>(responseBody, headers, status);
    }
}
