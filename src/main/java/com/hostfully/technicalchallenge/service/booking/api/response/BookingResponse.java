package com.hostfully.technicalchallenge.service.booking.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.hostfully.technicalchallenge.service.booking.data.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class BookingResponse {
  private UUID id;
  private UUID propertyId;
  private UUID userId;
  private LocalDate startDate;
  private LocalDate endDate;
  private Long totalPrice;
  @JsonFormat(shape = Shape.STRING)
  private BookingStatus status;
  private List<BookingGuestResponse> guests;
}
