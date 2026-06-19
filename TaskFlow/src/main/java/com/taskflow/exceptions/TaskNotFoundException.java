package com.taskflow.exceptions;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super("La tarea con credencial: " + message + "no ha sido encontrada. ");
    }
}
