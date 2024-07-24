package com.oscaris.kitchen.user.exceptions;
/*
*
@author ameda
@project kitchen-reader
*
*/

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException constraintViolationException){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid entry.Please check"+constraintViolationException.getMessage());
    }
}
