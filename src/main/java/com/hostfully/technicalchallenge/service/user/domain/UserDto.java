package com.hostfully.technicalchallenge.service.user.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class UserDto {
  private UUID id;
  private String name;
  private LocalDate dateOfBirth;
  private String email;
}
