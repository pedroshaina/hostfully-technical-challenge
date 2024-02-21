package com.hostfully.technicalchallenge.service.user.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
  private UUID id;
  private String name;
  private LocalDate dateOfBirth;
  private String email;
}
