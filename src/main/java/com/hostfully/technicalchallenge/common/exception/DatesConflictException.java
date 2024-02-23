package com.hostfully.technicalchallenge.common.exception;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public class DatesConflictException extends RuntimeException {

  private List<LocalDate> unavailableDates;

  public DatesConflictException(final String message) {
    super(message);
  }

  public DatesConflictException(final String message, final List<LocalDate> unavailableDates) {
    super(message);
    this.unavailableDates = unavailableDates;
  }

  public DatesConflictException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DatesConflictException(final String message, final Throwable cause, final List<LocalDate> unavailableDates) {
    super(message, cause);
    this.unavailableDates = unavailableDates;
  }

  public DatesConflictException(final Throwable cause) {
    super(cause);
  }

  public DatesConflictException(final Throwable cause, final List<LocalDate> unavailableDates) {
    super(cause);
    this.unavailableDates = unavailableDates;
  }
}
