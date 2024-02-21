package com.hostfully.technicalchallenge.service.user.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class UpsertUserRequest {
  @NotBlank
  private String name;
  @NotNull
  private LocalDate dateOfBirth;
  @NotBlank
  @Email
  private String email;
}
