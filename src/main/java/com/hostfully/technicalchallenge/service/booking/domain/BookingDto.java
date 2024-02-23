package com.hostfully.technicalchallenge.service.booking.domain;

import com.hostfully.technicalchallenge.service.booking.data.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@AllArgsConstructor
@With
public class BookingDto {
  private UUID id;
  private UUID propertyId;
  private UUID userId;
  private LocalDate startDate;
  private LocalDate endDate;
  private BookingStatus status;
  private Long totalPrice;
  private List<BookingGuestDto> guests;
}
