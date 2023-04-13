package com.mediscreen.patientmicroservice.exception_handler;

import java.time.LocalDateTime;
/**
 * Represents an error message returned by the API.
 *
 * @param statusCode  The HTTP status code of the error.
 * @param timestamp   The timestamp when the error occurred.
 * @param message     The error message.
 * @param description A description of the error.
 */
public record ErrorMessage(int statusCode, LocalDateTime timestamp, String message, String description) {
}
