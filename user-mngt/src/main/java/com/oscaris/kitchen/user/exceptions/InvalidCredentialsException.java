package com.oscaris.kitchen.user.exceptions;
/*
*
@author ameda
@project kitchen-reader
*
*/

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
