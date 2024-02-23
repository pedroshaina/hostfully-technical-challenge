package com.hostfully.technicalchallenge.service.booking.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class CreateBookingRequest {
  @NotNull
  private UUID propertyId;
  @NotNull
  private UUID userId;
  @NotNull
  private LocalDate startDate;
  @NotNull
  private LocalDate endDate;
  @NotNull
  @Valid
  private List<@NotNull UpsertBookingGuestsRequest> guests;
}
