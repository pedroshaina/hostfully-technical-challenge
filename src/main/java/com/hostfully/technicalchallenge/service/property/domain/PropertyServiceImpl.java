package com.hostfully.technicalchallenge.service.property.domain;

import com.hostfully.technicalchallenge.common.exception.DatesConflictException;
import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import com.hostfully.technicalchallenge.service.booking.data.Booking;
import com.hostfully.technicalchallenge.service.booking.data.BookingRepository;
import com.hostfully.technicalchallenge.service.property.data.Property;
import com.hostfully.technicalchallenge.service.property.data.PropertyRepository;
import com.hostfully.technicalchallenge.service.property.data.block.PropertyBlock;
import com.hostfully.technicalchallenge.service.property.data.block.PropertyBlockRepository;
import com.hostfully.technicalchallenge.service.user.data.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

  private final PropertyRepository propertyRepository;
  private final UserRepository userRepository;
  private final PropertyBlockRepository propertyBlockRepository;
  private final BookingRepository bookingRepository;
  private final PropertyMapper propertyMapper;

  @Override
  public PropertyDto retrieveProperty(final UUID propertyId) {
    Objects.requireNonNull(propertyId, "`propertyId` cannot be null");

    final Property retrieved = propertyRepository.findById(propertyId)
        .orElseThrow(() -> new NotFoundException(String.format("No property found with id %s", propertyId)));

    return propertyMapper.entityToDto(retrieved);
  }

  @Override
  public PropertyDto createProperty(final PropertyDto propertyInfo) {
    checkPropertyInfoIsNotNull(propertyInfo);

    userRepository.findById(propertyInfo.getOwnerUserId())
        .orElseThrow(() ->
            new NotFoundException(
                String.format(
                    "No user found with provided ownerUserId of '%s'",
                    propertyInfo.getOwnerUserId())));

    final Property toSave = propertyMapper.dtoToEntity(propertyInfo);
    final Property saved = propertyRepository.save(toSave);

    return propertyMapper.entityToDto(saved);
  }

  @Override
  public PropertyDto updateProperty(final UUID propertyId, final PropertyDto propertyInfo) {
    Objects.requireNonNull(propertyId, "`propertyId` cannot be null");
    checkPropertyInfoIsNotNull(propertyInfo);

    final Property retrieved = propertyRepository.findById(propertyId)
        .orElseThrow(() -> new NotFoundException(String.format("No property found with id %s", propertyId)));

    if (!propertyInfo.getOwnerUserId().equals(retrieved.getOwnerUserId())) {
      userRepository.findById(propertyInfo.getOwnerUserId())
          .orElseThrow(() ->
              new NotFoundException(
                String.format(
                    "No user found with provided ownerUserId of '%s'",
                    propertyInfo.getOwnerUserId())));
    }

    retrieved.setName(propertyInfo.getName());
    retrieved.setOwnerUserId(propertyInfo.getOwnerUserId());
    retrieved.setAddressLine1(propertyInfo.getAddressLine1());
    retrieved.setAddressLine2(propertyInfo.getAddressLine2());
    retrieved.setCity(propertyInfo.getCity());
    retrieved.setState(propertyInfo.getState());
    retrieved.setCountry(propertyInfo.getCountry());
    retrieved.setPostalCode(propertyInfo.getPostalCode());
    retrieved.setPricePerGuest(propertyInfo.getPricePerGuest());

    final Property saved = propertyRepository.save(retrieved);

    return propertyMapper.entityToDto(saved);
  }

  @Override
  public void deleteProperty(final UUID propertyId) {
    Objects.requireNonNull(propertyId, "`propertyId` cannot be null");

    propertyBlockRepository.deleteByPropertyId(propertyId);
    propertyRepository.deleteById(propertyId);
  }

  @Override
  public void blockPropertyDates(
      final UUID propertyId,
      final Set<LocalDate> datesToBlock,
      final String reason) {

    Objects.requireNonNull(propertyId, "`propertyId` cannot be null");

    if (Objects.isNull(datesToBlock) || datesToBlock.isEmpty()) {
      return;
    }

    propertyRepository.findById(propertyId)
        .orElseThrow(() -> new NotFoundException(String.format("No property found with id %s", propertyId)));

    datesToBlock.forEach(date -> createOrUpdatePropertyBlock(propertyId, date, reason));
  }

  @Override
  public void unblockPropertyDates(final UUID propertyId, final Set<LocalDate> datesToUnblock) {
    Objects.requireNonNull(propertyId, "`propertyId` cannot be null");

    if (Objects.isNull(datesToUnblock) || datesToUnblock.isEmpty()) {
      return;
    }

    propertyBlockRepository.deleteByPropertyIdAndDateIn(propertyId, new ArrayList<>(datesToUnblock));
  }

  @Override
  public List<PropertyBlockDto> retrievePropertyBlockedDates(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate) {

    Objects.requireNonNull(propertyId, "`propertyId` cannot be null");
    Objects.requireNonNull(startDate, "`startDate` cannot be null");
    Objects.requireNonNull(endDate, "`endDate` cannot be null");

    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("startDate cannot be after endDate");
    }

    final List<PropertyBlock> blockedDates =
        propertyBlockRepository.findByPropertyIdAndDateBetween(propertyId, startDate, endDate);

    return blockedDates.stream()
        .map(propertyMapper::entityToDto)
        .toList();
  }

  @Override
  public List<LocalDate> retrievePropertyUnavailableDatesForPeriod(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate) {

    Objects.requireNonNull(propertyId, "`propertyId` cannot be null");
    Objects.requireNonNull(startDate, "`startDate` cannot be null");
    Objects.requireNonNull(endDate, "`endDate` cannot be null");

    final Set<LocalDate> uniqueDates = new HashSet<>();

    uniqueDates.addAll(retrievePropertyBlocksAsDates(propertyId, startDate, endDate));
    uniqueDates.addAll(retrieveBookedDates(propertyId, startDate, endDate));

    return new ArrayList<>(uniqueDates);
  }

  private void checkPropertyInfoIsNotNull(final PropertyDto propertyInfo) {
    Objects.requireNonNull(propertyInfo, "`propertyInfo` cannot be null");
    Objects.requireNonNull(propertyInfo.getName(), "`propertyInfo.getName()` cannot be null");
    Objects.requireNonNull(propertyInfo.getOwnerUserId(), "`propertyInfo.getOwnerUserId()` cannot be null");
    Objects.requireNonNull(propertyInfo.getAddressLine1(), "`propertyInfo.getAddressLine1()` cannot be null");
    Objects.requireNonNull(propertyInfo.getCity(), "`propertyInfo.getCity()` cannot be null");
    Objects.requireNonNull(propertyInfo.getState(), "`propertyInfo.getState()` cannot be null");
    Objects.requireNonNull(propertyInfo.getCountry(), "`propertyInfo.getCountry()` cannot be null");
    Objects.requireNonNull(propertyInfo.getPostalCode(), "`propertyInfo.getPostalCode()` cannot be null");
    Objects.requireNonNull(propertyInfo.getPricePerGuest(), "`propertyInfo.getPricePerGuest()` cannot be null");
  }

  private void createOrUpdatePropertyBlock(
      final UUID propertyId,
      final LocalDate date,
      final String reason) {

    final Optional<PropertyBlock> existing = propertyBlockRepository.findByPropertyIdAndDate(propertyId, date);

    if (existing.isPresent()) {
      //If date is already blocked for given property, update reason
      final PropertyBlock toUpdate = existing.get();
      toUpdate.setReason(reason);
      propertyBlockRepository.save(toUpdate);

      return;
    }

    checkIfDateIsBooked(propertyId, date);

    final PropertyBlock toCreate = PropertyBlock.builder()
        .propertyId(propertyId)
        .date(date)
        .reason(reason)
        .build();

    propertyBlockRepository.save(toCreate);
  }

  private void checkIfDateIsBooked(final UUID propertyId, final LocalDate date) {
    final List<LocalDate> bookedDates = retrieveBookedDates(propertyId, date, date);

    if (!bookedDates.isEmpty()) {
      throw new DatesConflictException("Cannot block dates that overlaps with active bookings");
    }
  }

  private List<LocalDate> retrievePropertyBlocksAsDates(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate) {

    final List<PropertyBlock> blockedDates =
        propertyBlockRepository.findByPropertyIdAndDateBetween(propertyId, startDate, endDate);

    return blockedDates.stream()
        .map(PropertyBlock::getDate)
        .toList();
  }

  private List<LocalDate> retrieveBookedDates(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate) {

    final List<Booking> overlappingBookings =
        bookingRepository.findNonCanceledBookingsOverlappingWithDates(propertyId, startDate, endDate);

    return overlappingBookings.stream()
        .flatMap(booking ->
            booking.getStartDate()
                .datesUntil(booking.getEndDate().plusDays(1L)))
        .filter(date -> date.isBefore(endDate) || date.isEqual(endDate))
        .filter(date -> date.isAfter(startDate) || date.isEqual(startDate))
        .toList();
  }
}
