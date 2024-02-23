package com.hostfully.technicalchallenge.service.property.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class UpsertPropertyRequest {
  @NotBlank
  private String name;
  @NotNull
  private UUID ownerUserId;
  @NotBlank
  private String addressLine1;
  private String addressLine2;
  @NotBlank
  private String city;
  @NotBlank
  private String state;
  @NotBlank
  private String country;
  @NotBlank
  private String postalCode;
  @NotNull
  @Positive
  private Long pricePerGuest;
}
