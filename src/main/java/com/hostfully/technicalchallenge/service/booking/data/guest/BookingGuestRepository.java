package com.hostfully.technicalchallenge.service.booking.data.guest;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingGuestRepository extends JpaRepository<BookingGuest, UUID> {
  void deleteByBookingId(final UUID bookingId);
}
