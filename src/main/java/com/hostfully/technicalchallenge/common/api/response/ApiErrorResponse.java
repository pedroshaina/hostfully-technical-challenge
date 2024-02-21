package com.hostfully.technicalchallenge.common.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ApiErrorResponse {
  private int statusCode;
  private String message;
  private Instant timestamp;
  private Map<String, String> fieldValidationErrors;

  public ApiErrorResponse(final int statusCode, final String message) {
    this.statusCode = statusCode;
    this.message = message;
    this.timestamp = Instant.now();
  }

  public void addFieldValidationError(final String field, final String errorDescription) {
    if (Objects.isNull(this.fieldValidationErrors)) {
      this.fieldValidationErrors = new HashMap<>();
    }

    this.fieldValidationErrors.put(field, errorDescription);
  }
}
