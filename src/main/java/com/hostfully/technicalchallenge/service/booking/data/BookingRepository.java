package com.hostfully.technicalchallenge.service.booking.data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

  @Query("""
    from Booking b
    where b.propertyId = :propertyId
    and b.status != 'CANCELED'
    and (b.startDate  <= :endDate and b.endDate >= :startDate)
  """)
  List<Booking> findNonCanceledBookingsOverlappingWithDates(
      @Param("propertyId") final UUID propertyId,
      @Param("startDate") final LocalDate startDate,
      @Param("endDate") final LocalDate endDate);
}
