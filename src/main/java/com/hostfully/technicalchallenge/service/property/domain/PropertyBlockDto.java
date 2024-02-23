package com.hostfully.technicalchallenge.service.property.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class PropertyBlockDto {
  private UUID id;
  private UUID propertyId;
  private LocalDate date;
  private String reason;
}
