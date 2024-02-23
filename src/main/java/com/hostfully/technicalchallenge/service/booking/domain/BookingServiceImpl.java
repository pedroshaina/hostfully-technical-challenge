package com.hostfully.technicalchallenge.service.booking.domain;

import com.hostfully.technicalchallenge.common.exception.DatesConflictException;
import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import com.hostfully.technicalchallenge.service.booking.data.Booking;
import com.hostfully.technicalchallenge.service.booking.data.BookingRepository;
import com.hostfully.technicalchallenge.service.booking.data.BookingStatus;
import com.hostfully.technicalchallenge.service.booking.data.guest.BookingGuest;
import com.hostfully.technicalchallenge.service.booking.data.guest.BookingGuestRepository;
import com.hostfully.technicalchallenge.service.property.data.Property;
import com.hostfully.technicalchallenge.service.property.data.PropertyRepository;
import com.hostfully.technicalchallenge.service.property.domain.PropertyService;
import com.hostfully.technicalchallenge.service.user.data.UserRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

  private final PropertyService propertyService;
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final PropertyRepository propertyRepository;
  private final BookingGuestRepository bookingGuestRepository;
  private final BookingMapper bookingMapper;

  @Override
  public BookingDto retrieveBooking(final UUID bookingId) {
    Objects.requireNonNull(bookingId, "`bookingId` cannot be null");

    final Booking retrievedBooking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException(String.format("No booking found with id %s", bookingId)));

    return bookingMapper.entityToDto(retrievedBooking);
  }

  @Override
  public BookingDto createBooking(final BookingDto bookingInfo) {
    checkBookingInfoIsNotNullOnCreate(bookingInfo);

    if (bookingInfo.getGuests().isEmpty()) {
      throw new IllegalArgumentException("Cannot create booking without guests information");
    }

    userRepository.findById(bookingInfo.getUserId())
        .orElseThrow(() -> new NotFoundException(String.format("No user found with provided userId %s", bookingInfo.getUserId())));

    final Property property = propertyRepository.findById(bookingInfo.getPropertyId())
        .orElseThrow(() -> new NotFoundException(String.format("No property found with provided propertyId %s", bookingInfo.getPropertyId())));

    checkDatesAvailability(bookingInfo.getStartDate(), bookingInfo.getEndDate(), property.getId());

    final long totalPrice = calculateTotalPrice(bookingInfo, property);

    final Booking toSave = bookingMapper.dtoToEntity(bookingInfo);

    toSave.setStatus(BookingStatus.BOOKED);
    toSave.setTotalPrice(totalPrice);

    final Booking saved = bookingRepository.save(toSave);

    final List<BookingGuest> savedGuests = saveGuests(saved, bookingInfo.getGuests());

    saved.setGuests(savedGuests);

    return bookingMapper.entityToDto(saved);
  }

  @Override
  public BookingDto updateBooking(final UUID bookingId, final BookingDto bookingInfo) {
    checkBookingInfoIsNotNullOnUpdate(bookingInfo);

    if (bookingInfo.getGuests().isEmpty()) {
      throw new IllegalArgumentException("Cannot update booking without guests information");
    }

    final Booking retrievedBooking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException(String.format("No booking found with id %s", bookingId)));

    if (retrievedBooking.getStatus() == BookingStatus.CANCELED) {
      throw new IllegalStateException("Cannot update canceled booking");
    }

    final Property property = propertyRepository.findById(retrievedBooking.getPropertyId())
        .orElseThrow(() -> new NotFoundException(String.format("No property found with provided propertyId %s", retrievedBooking.getPropertyId())));

    checkDatesAvailability(bookingInfo.getStartDate(), bookingInfo.getEndDate(), property.getId());

    final long totalPrice = calculateTotalPrice(bookingInfo, property);

    retrievedBooking.setStartDate(bookingInfo.getStartDate());
    retrievedBooking.setEndDate(bookingInfo.getEndDate());
    retrievedBooking.setTotalPrice(totalPrice);

    final Booking saved = bookingRepository.save(retrievedBooking);

    final List<BookingGuest> guests = saveGuests(saved, bookingInfo.getGuests());

    saved.setGuests(guests);

    return bookingMapper.entityToDto(saved);
  }

  @Override
  public BookingDto cancelBooking(final UUID bookingId) {
    final Booking retrievedBooking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException(String.format("No booking found with id %s", bookingId)));

    if (retrievedBooking.getStatus() == BookingStatus.CANCELED) {
      throw new IllegalStateException("Cannot cancel an already canceled booking");
    }

    retrievedBooking.setStatus(BookingStatus.CANCELED);

    final Booking saved = bookingRepository.save(retrievedBooking);

    return bookingMapper.entityToDto(saved);
  }

  @Override
  public BookingDto rebookCanceledBooking(final UUID bookingId) {
    final Booking retrievedBooking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException(String.format("No booking found with id %s", bookingId)));

    if (retrievedBooking.getStatus() != BookingStatus.CANCELED) {
      throw new IllegalStateException("Can only rebook a canceled booking");
    }

    checkDatesAvailability(
        retrievedBooking.getStartDate(),
        retrievedBooking.getEndDate(),
        retrievedBooking.getPropertyId());

    retrievedBooking.setStatus(BookingStatus.BOOKED);

    final Booking saved = bookingRepository.save(retrievedBooking);

    return bookingMapper.entityToDto(saved);
  }

  @Override
  public void deleteBooking(final UUID bookingId) {
    bookingGuestRepository.deleteByBookingId(bookingId);
    bookingRepository.deleteById(bookingId);
  }

  private void checkBookingInfoIsNotNullOnCreate(final BookingDto bookingInfo) {
    Objects.requireNonNull(bookingInfo, "`bookingInfo` cannot be null");
    Objects.requireNonNull(bookingInfo.getPropertyId(), "`bookingInfo.getPropertyId()` cannot be null");
    Objects.requireNonNull(bookingInfo.getUserId(), "`bookingInfo.getUserId()` cannot be null");
    Objects.requireNonNull(bookingInfo.getStartDate(), "`bookingInfo.getStartDate()` cannot be null");
    Objects.requireNonNull(bookingInfo.getEndDate(), "`bookingInfo.getEndDate()` cannot be null");
    Objects.requireNonNull(bookingInfo.getGuests(), "`bookingInfo.getGuests()` cannot be null");
  }

  private List<BookingGuest> saveGuests(final Booking booking, final List<BookingGuestDto> guests) {
    bookingGuestRepository.deleteByBookingId(booking.getId());

    final List<BookingGuest> guestsToSave = guests.stream()
            .map(guest -> {
              final String name =
                  Objects.requireNonNull(guest.getName(), "`guest.getName()` cannot be null");

              final LocalDate dateOfBirth =
                  Objects.requireNonNull(guest.getDateOfBirth(), "`guest.getDateOfBirth()` cannot be null");

              return BookingGuest.builder()
                  .bookingId(booking.getId())
                  .name(name)
                  .dateOfBirth(dateOfBirth)
                  .build();
            })
            .toList();

    return bookingGuestRepository.saveAll(guestsToSave);
  }

  private void checkBookingInfoIsNotNullOnUpdate(final BookingDto bookingInfo) {
    Objects.requireNonNull(bookingInfo, "`bookingInfo` cannot be null");
    Objects.requireNonNull(bookingInfo.getStartDate(), "`bookingInfo.getStartDate()` cannot be null");
    Objects.requireNonNull(bookingInfo.getEndDate(), "`bookingInfo.getEndDate()` cannot be null");
    Objects.requireNonNull(bookingInfo.getGuests(), "`bookingInfo.getGuests()` cannot be null");
  }

  private void checkDatesAvailability(final LocalDate startDate, final LocalDate endDate, final UUID propertyId) {
    final List<LocalDate> propertyUnavailableDates =
        propertyService.retrievePropertyUnavailableDatesForPeriod(
            propertyId,
            startDate,
            endDate);

    if (!propertyUnavailableDates.isEmpty()) {
      throw new DatesConflictException("The booking dates contains unavailable dates for the selected property", propertyUnavailableDates);
    }
  }

  private long calculateTotalPrice(final BookingDto bookingInfo, final Property property) {
    final int guestCount = bookingInfo.getGuests().size();

    final long daysCount = ChronoUnit.DAYS.between(
        bookingInfo.getStartDate(),
        bookingInfo.getEndDate().plusDays(1L));

    final long pricePerGuestPerDay = property.getPricePerGuest();

    return pricePerGuestPerDay * daysCount * guestCount;
  }
}
