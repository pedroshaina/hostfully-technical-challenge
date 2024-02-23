package com.hostfully.technicalchallenge.service.property.api.response;

import java.util.UUID;
import lombok.Data;

@Data
public class PropertyResponse {
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
