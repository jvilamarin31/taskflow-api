package com.taskflow.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super("El usuario con credencial: " + message + " no ha sido encontrado. ");
    }
}
