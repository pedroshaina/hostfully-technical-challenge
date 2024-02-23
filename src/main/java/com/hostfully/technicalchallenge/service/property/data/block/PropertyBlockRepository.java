package com.hostfully.technicalchallenge.service.property.data.block;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyBlockRepository extends JpaRepository<PropertyBlock, UUID> {
  List<PropertyBlock> findByPropertyIdAndDateBetween(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate);
  Optional<PropertyBlock> findByPropertyIdAndDate(final UUID propertyId, final LocalDate date);

  void deleteByPropertyIdAndDateIn(final UUID propertyId, final List<LocalDate> dates);

  void deleteByPropertyId(final UUID propertyId);
}
