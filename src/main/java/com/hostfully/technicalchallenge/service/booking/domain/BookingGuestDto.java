package com.hostfully.technicalchallenge.service.booking.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class BookingGuestDto {
  private UUID id;
  private UUID bookingId;
  private String name;
  private LocalDate dateOfBirth;
}
