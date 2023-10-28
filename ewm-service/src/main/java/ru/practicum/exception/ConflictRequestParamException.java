package ru.practicum.exception;

public class ConflictRequestParamException extends RuntimeException {
    public ConflictRequestParamException(String message) {
        super(message);
    }
}
