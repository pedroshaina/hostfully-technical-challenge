package com.hostfully.technicalchallenge.service.property.api.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class UpsertPropertyBlockRequest {
  @NotNull
  private Set<@NotNull LocalDate> dates;
  private String reason;
}
