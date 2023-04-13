package com.mediscreen.patientmicroservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Patient already exists in DB!")
public class PatientAlreadyExistException extends RuntimeException {
    public PatientAlreadyExistException(String s) {
        super(s);
    }
}
