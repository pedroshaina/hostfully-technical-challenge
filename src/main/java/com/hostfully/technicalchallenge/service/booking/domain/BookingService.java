package com.hostfully.technicalchallenge.service.booking.domain;

import java.util.UUID;

public interface BookingService {
  BookingDto retrieveBooking(final UUID bookingId);

  BookingDto createBooking(final BookingDto bookingInfo);

  BookingDto updateBooking(final UUID bookingId, final BookingDto bookingInfo);

  BookingDto cancelBooking(final UUID bookingId);

  BookingDto rebookCanceledBooking(final UUID bookingId);

  void deleteBooking(final UUID bookingId);
}
