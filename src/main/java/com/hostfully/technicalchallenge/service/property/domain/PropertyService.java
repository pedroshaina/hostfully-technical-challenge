package com.hostfully.technicalchallenge.service.property.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PropertyService {
  PropertyDto retrieveProperty(final UUID propertyId);

  PropertyDto createProperty(final PropertyDto propertyInfo);

  PropertyDto updateProperty(final UUID propertyId, final PropertyDto propertyInfo);

  void deleteProperty(final UUID propertyId);

  void blockPropertyDates(
      final UUID propertyId,
      final Set<LocalDate> datesToBlock,
      final String reason);

  void unblockPropertyDates(final UUID propertyId, final Set<LocalDate> datesToUnblock);

  List<PropertyBlockDto> retrievePropertyBlockedDates(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate);

  List<LocalDate> retrievePropertyUnavailableDatesForPeriod(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate);
}
