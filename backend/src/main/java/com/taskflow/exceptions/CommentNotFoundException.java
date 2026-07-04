package com.taskflow.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super("El comentario con credencial: " + message + " no ha sido encontrado. ");
    }
}
