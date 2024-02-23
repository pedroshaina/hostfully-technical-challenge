package com.hostfully.technicalchallenge.service.booking.api.response;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class BookingGuestResponse {
  private UUID id;
  private String name;
  private LocalDate dateOfBirth;
}
