package com.hostfully.technicalchallenge.service.booking.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class UpsertBookingGuestsRequest {
  @NotBlank
  private String name;
  @NotNull
  private LocalDate dateOfBirth;
}
