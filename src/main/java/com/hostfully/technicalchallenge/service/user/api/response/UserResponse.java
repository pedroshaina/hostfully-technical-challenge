package com.hostfully.technicalchallenge.service.user.api.response;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class UserResponse {
  private UUID id;
  private String name;
  private LocalDate dateOfBirth;
  private String email;
}
