package org.example.deferreddemo.controllers.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse (HttpStatus status, String message) {}
