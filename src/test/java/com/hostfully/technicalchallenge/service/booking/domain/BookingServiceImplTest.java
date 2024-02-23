package com.hostfully.technicalchallenge.service.booking.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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
import com.hostfully.technicalchallenge.service.user.data.User;
import com.hostfully.technicalchallenge.service.user.data.UserRepository;
import com.hostfully.technicalchallenge.util.RandomEntityGenerator;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
  @Mock
  private PropertyService propertyService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BookingRepository bookingRepository;
  @Mock
  private PropertyRepository propertyRepository;
  @Mock
  private BookingGuestRepository bookingGuestRepository;
  @Spy
  private BookingMapper bookingMapper = new BookingMapperImpl();
  @InjectMocks
  private BookingServiceImpl bookingService;

  @Test
  void shouldThrowNullPointerExceptionIfBookingIdIsNullWhenRetrieveBooking() {
    assertThatThrownBy(() -> bookingService.retrieveBooking(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfBookingDoesntExistWhenRetrieveBooking() {
    doReturn(Optional.empty()).when(bookingRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.retrieveBooking(UUID.randomUUID()))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldRetrieveBooking() {
    final UUID bookingId = UUID.randomUUID();

    final Booking booking = RandomEntityGenerator.create(Booking.class)
        .withId(bookingId);

    doReturn(Optional.of(booking)).when(bookingRepository).findById(any(UUID.class));

    final BookingDto retrieved = bookingService.retrieveBooking(bookingId);
    final BookingDto expected = bookingMapper.entityToDto(booking);

    assertThat(retrieved).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfBookingInfoIsNullWhenCreateBooking() {
    assertThatThrownBy(() -> bookingService.createBooking(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
            .withPropertyId(null);

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfUserIdIsNullWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withUserId(null);

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfStartDateIsNullWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withStartDate(null);

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfEndDateIsNullWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withEndDate(null);

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfGuestsIsNullWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withGuests(null);

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfGuestsIsEmptyWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withGuests(Collections.emptyList());

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfUserDoesntExistWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class);

    doReturn(Optional.empty()).when(userRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfPropertyDoesntExistWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class);

    final User user = RandomEntityGenerator.create(User.class);

    doReturn(Optional.of(user)).when(userRepository).findById(any(UUID.class));
    doReturn(Optional.empty()).when(propertyRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowDatesConflictExceptionIfDatesAreUnavailableWhenCreateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withStartDate(LocalDate.now().minusDays(1L))
        .withEndDate(LocalDate.now().plusDays(1L));

    final User user = RandomEntityGenerator.create(User.class);
    final Property property = RandomEntityGenerator.create(Property.class)
        .withPricePerGuest(10000L);

    doReturn(Optional.of(user)).when(userRepository).findById(any(UUID.class));
    doReturn(Optional.of(property)).when(propertyRepository).findById(any(UUID.class));

    doReturn(List.of(LocalDate.now())).when(propertyService)
        .retrievePropertyUnavailableDatesForPeriod(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    assertThatThrownBy(() -> bookingService.createBooking(bookingInfo))
        .isInstanceOf(DatesConflictException.class);
  }

  @Test
  void shouldCreateBookingAndGuests() {
    final UUID bookingId = UUID.randomUUID();

    final BookingGuestDto bookingGuest = RandomEntityGenerator.create(BookingGuestDto.class);

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withStartDate(LocalDate.now().minusDays(1L))
        .withEndDate(LocalDate.now().plusDays(1L))
        .withGuests(List.of(bookingGuest));

    final UUID bookingGuestId = bookingGuest.getId();

    final User user = RandomEntityGenerator.create(User.class);
    final Property property = RandomEntityGenerator.create(Property.class)
        .withPricePerGuest(10000L);

    doReturn(Optional.of(user)).when(userRepository).findById(any(UUID.class));
    doReturn(Optional.of(property)).when(propertyRepository).findById(any(UUID.class));

    doReturn(Collections.emptyList()).when(propertyService)
        .retrievePropertyUnavailableDatesForPeriod(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    doAnswer(invocation -> {
      final Booking bookingToBeSaved = invocation.getArgument(0, Booking.class);
      return bookingToBeSaved.withId(bookingId);
    }).when(bookingRepository).save(any(Booking.class));

    doAnswer(invocation -> {
      final List<BookingGuest> guestsToBeSaved = invocation.getArgument(0);

      return guestsToBeSaved.stream()
          .map(guest -> guest.withId(bookingGuestId).withBookingId(bookingId))
          .toList();
    }).when(bookingGuestRepository).saveAll(anyList());

    final BookingDto saved = bookingService.createBooking(bookingInfo);
    final BookingDto expected = bookingInfo.withId(bookingId)
        .withStatus(BookingStatus.BOOKED)
        .withTotalPrice(30000L)
        .withGuests(List.of(bookingGuest.withBookingId(bookingId)));

    assertThat(saved).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfBookingIdIsNullWhenUpdateBooking() {
    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class);

    assertThatThrownBy(() -> bookingService.updateBooking(null, bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfBookingInfoIsNullWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfStartDateIsNullWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withStartDate(null);

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfEndDateIsNullWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withEndDate(null);

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfGuestsIsNullWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withGuests(null);

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfGuestsIsEmptyWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withGuests(Collections.emptyList());

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfBookingDoesntExistWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class);

    doReturn(Optional.empty()).when(bookingRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowIllegalStateExceptionIfBookingIsCanceledWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class);

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
            .withStatus(BookingStatus.CANCELED);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowEntityNotFoundExceptionIfPropertyDoesntExistWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class);

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
        .withStatus(BookingStatus.BOOKED);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));
    doReturn(Optional.empty()).when(propertyRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowDatesConflictExceptionIfDatesAreUnavailableWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class);

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
        .withStatus(BookingStatus.BOOKED);

    final Property retrievedProperty = RandomEntityGenerator.create(Property.class)
        .withPricePerGuest(10000L);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));
    doReturn(Optional.of(retrievedProperty)).when(propertyRepository).findById(any(UUID.class));

    doReturn(List.of(LocalDate.now())).when(propertyService)
        .retrievePropertyUnavailableDatesForPeriod(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    assertThatThrownBy(() -> bookingService.updateBooking(bookingId, bookingInfo))
        .isInstanceOf(DatesConflictException.class);
  }

  @Test
  void shouldUpdateBookingDatesAndGuests() {
    final UUID bookingId = UUID.randomUUID();

    final BookingGuestDto bookingGuest = RandomEntityGenerator.create(BookingGuestDto.class);

    final BookingDto bookingInfo = RandomEntityGenerator.create(BookingDto.class)
        .withStartDate(LocalDate.now().minusDays(1L))
        .withEndDate(LocalDate.now().plusDays(1L))
        .withGuests(List.of(bookingGuest));

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
        .withStatus(BookingStatus.BOOKED);

    final Property retrievedProperty = RandomEntityGenerator.create(Property.class)
        .withPricePerGuest(10000L);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));
    doReturn(Optional.of(retrievedProperty)).when(propertyRepository).findById(any(UUID.class));

    doReturn(Collections.emptyList()).when(propertyService)
        .retrievePropertyUnavailableDatesForPeriod(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    doAnswer(AdditionalAnswers.returnsFirstArg()).when(bookingRepository).save(any(Booking.class));
    doAnswer(invocation -> {
      final List<BookingGuest> guestsToBeSaved = invocation.getArgument(0);

      return guestsToBeSaved.stream()
          .map(guest -> guest.withId(bookingGuest.getId()).withBookingId(bookingId))
          .toList();
    }).when(bookingGuestRepository).saveAll(anyList());

    final BookingDto updated = bookingService.updateBooking(bookingId, bookingInfo);
    final BookingDto expected = bookingMapper.entityToDto(retrievedBooking)
        .withGuests(List.of(bookingGuest.withBookingId(bookingId)))
        .withStartDate(bookingInfo.getStartDate())
        .withEndDate(bookingInfo.getEndDate());

    assertThat(updated).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfBookingIdIsNullWhenCancelBooking() {
    assertThatThrownBy(() -> bookingService.cancelBooking(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfBookingDoesntExistWhenCancelBooking() {
    final UUID bookingId = UUID.randomUUID();

    doReturn(Optional.empty()).when(bookingRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.cancelBooking(bookingId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowIllegalStateExceptionIfBookingIsAlreadyCanceledWhenCancelBooking() {
    final UUID bookingId = UUID.randomUUID();

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
            .withStatus(BookingStatus.CANCELED);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.cancelBooking(bookingId))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldCancelBooking() {
    final UUID bookingId = UUID.randomUUID();

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
        .withStatus(BookingStatus.BOOKED);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));
    doAnswer(AdditionalAnswers.returnsFirstArg()).when(bookingRepository).save(any(Booking.class));

    final BookingDto canceled = bookingService.cancelBooking(bookingId);
    final BookingDto expected = bookingMapper.entityToDto(retrievedBooking)
        .withStatus(BookingStatus.CANCELED);

    assertThat(canceled).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfBookingIdIsNullWhenRebookCanceledBooking() {
    assertThatThrownBy(() -> bookingService.rebookCanceledBooking(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfBookingDoesntExistWhenRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    doReturn(Optional.empty()).when(bookingRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.rebookCanceledBooking(bookingId))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowIllegalStateExceptionIfBookingIsNotCanceledWhenRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
            .withStatus(BookingStatus.BOOKED);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> bookingService.rebookCanceledBooking(bookingId))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowDatesConflictExceptionIfDatesAreUnavailableWhenRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
        .withStatus(BookingStatus.CANCELED);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));

    doReturn(List.of(LocalDate.now())).when(propertyService)
        .retrievePropertyUnavailableDatesForPeriod(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    assertThatThrownBy(() -> bookingService.rebookCanceledBooking(bookingId))
        .isInstanceOf(DatesConflictException.class);
  }

  @Test
  void shouldRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    final Booking retrievedBooking = RandomEntityGenerator.create(Booking.class)
        .withStatus(BookingStatus.CANCELED);

    doReturn(Optional.of(retrievedBooking)).when(bookingRepository).findById(any(UUID.class));

    doReturn(Collections.emptyList()).when(propertyService)
        .retrievePropertyUnavailableDatesForPeriod(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    doAnswer(AdditionalAnswers.returnsFirstArg()).when(bookingRepository).save(any(Booking.class));

    final BookingDto rebooked = bookingService.rebookCanceledBooking(bookingId);
    final BookingDto expected = bookingMapper.entityToDto(retrievedBooking)
        .withStatus(BookingStatus.BOOKED);

    assertThat(rebooked).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfBookingIdIsNullWhenDeleteBooking() {
    assertThatThrownBy(() -> bookingService.deleteBooking(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldDeleteBookingAndGuests() {
    final UUID bookingId = UUID.randomUUID();

    bookingService.deleteBooking(bookingId);

    verify(bookingGuestRepository).deleteByBookingId(any(UUID.class));
    verify(bookingRepository).deleteById(any(UUID.class));
  }
}
