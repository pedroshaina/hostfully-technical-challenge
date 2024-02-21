package com.hostfully.technicalchallenge.service.user.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class UserDto {
  private UUID id;
  private String name;
  private LocalDate dateOfBirth;
  private String email;
}
