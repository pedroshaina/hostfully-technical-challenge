package com.hostfully.technicalchallenge.common.api;

import com.hostfully.technicalchallenge.common.api.response.ApiErrorResponse;
import com.hostfully.technicalchallenge.common.exception.DatesConflictException;
import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiErrorHandler {

  @ExceptionHandler(value = NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorResponse handleNotFoundException(final NotFoundException e) {
    return new ApiErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        e.getMessage()
    );
  }

  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {

    final String exceptionErrorDetails = e.getCause().getMessage();

    return new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        String.format("Error parsing the JSON object: %s", exceptionErrorDetails)
    );
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ApiErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

    final ApiErrorResponse validationError = new ApiErrorResponse(
        HttpStatus.UNPROCESSABLE_ENTITY.value(),
        "One or more fields have an error"
    );

    e.getBindingResult().getFieldErrors().forEach(error -> {
      validationError.addFieldValidationError(error.getField(), error.getDefaultMessage());
    });

    return validationError;
  }

  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
    return new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        String.format(
            "The provided parameter {%s} with value %s must be of type %s",
            e.getName(),
            e.getValue(),
            e.getRequiredType()
        )
    );
  }

  @ExceptionHandler(value = NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ApiErrorResponse handleNoResourceFoundException(final NoResourceFoundException e) {
    return new ApiErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        String.format("The provided url '/%s' does not exist", e.getResourcePath())
    );
  }

  @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ApiErrorResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e, final HttpServletResponse response) {
    response.addHeader(HttpHeaders.ALLOW, String.join(",", e.getSupportedMethods()));

    return new ApiErrorResponse(
        HttpStatus.METHOD_NOT_ALLOWED.value(),
        e.getMessage()
    );
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ApiErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
    return new ApiErrorResponse(
        HttpStatus.UNPROCESSABLE_ENTITY.value(),
        e.getMessage()
    );
  }

  @ExceptionHandler(value = IllegalStateException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ApiErrorResponse handleIllegalStateException(final IllegalStateException e) {
    return new ApiErrorResponse(
        HttpStatus.UNPROCESSABLE_ENTITY.value(),
        e.getMessage()
    );
  }

  @ExceptionHandler(value = DatesConflictException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ApiErrorResponse handleDatesConflictException(final DatesConflictException e) {
    final StringBuilder builder = new StringBuilder();

    builder.append(e.getMessage());

    if (Objects.nonNull(e.getUnavailableDates()) && !e.getUnavailableDates().isEmpty()) {
      Collections.sort(e.getUnavailableDates());
      builder.append(String.format(". The unavailable dates are: %s", e.getUnavailableDates()));
    }

    final String message = builder.toString();

    return new ApiErrorResponse(
        HttpStatus.UNPROCESSABLE_ENTITY.value(),
        message
    );
  }

  @ExceptionHandler(value = Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiErrorResponse handleAllExceptions(final Exception e) {
    return new ApiErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        e.getMessage()
    );
  }
}
