package com.hostfully.technicalchallenge.service.property.api.response;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class PropertyBlockResponse {
  private UUID id;
  private UUID propertyId;
  private LocalDate date;
  private String reason;
}
