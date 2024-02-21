package com.hostfully.technicalchallenge.service.user.api.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UpsertUserRequest {
  private String name;
  private LocalDate dateOfBirth;
  private String email;
}
