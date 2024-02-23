package com.hostfully.technicalchallenge.service.property.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class PropertyDto {
  private UUID id;
  private String name;
  private UUID ownerUserId;
  private String addressLine1;
  private String addressLine2;
  private String city;
  private String state;
  private String country;
  private String postalCode;
  private Long pricePerGuest;
}
