package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.event.mapper.EventMapper.FORMATTER;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAlreadyExistsException(final AlreadyExistException e) {
        return new ApiError(
                List.of(e),
                e.getMessage(),
                "this object is already exist in db",
                "CONFLICT",
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return new ApiError(
                List.of(e),
                e.getMessage(),
                "this object not found in db",
                "NOT_FOUND",
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidateException(final ValidationException e) {
        return new ApiError(
                List.of(e),
                e.getMessage(),
                "bad request params",
                "BAD_REQUEST",
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final AccessDeniedException e) {
        return new ApiError(
                List.of(e),
                e.getMessage(),
                "you don't have access to these object or data",
                "FORBIDDEN",
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(final Throwable e) {
        return new ApiError(
                new ArrayList<>(),
                e.getMessage(),
                "server error",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now().format(FORMATTER));
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ApiError(
                new ArrayList<>(),
                e.getMessage(),
                "validation error",
                "BAD_REQUEST",
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final javax.validation.ValidationException e) {
        return new ApiError(new ArrayList<>(),
                e.getMessage(),
                "validation error",
                "BAD_REQUEST",
                LocalDateTime.now().format(FORMATTER));
    }
}
