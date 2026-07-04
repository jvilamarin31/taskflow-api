package com.taskflow.exceptions;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super("El proyecto con credencial: " + message + " no ha sido encontrado. ");
    }
}
