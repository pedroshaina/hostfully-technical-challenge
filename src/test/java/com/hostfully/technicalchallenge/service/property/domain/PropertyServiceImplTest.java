package com.hostfully.technicalchallenge.service.property.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.hostfully.technicalchallenge.common.exception.DatesConflictException;
import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import com.hostfully.technicalchallenge.service.booking.data.Booking;
import com.hostfully.technicalchallenge.service.booking.data.BookingRepository;
import com.hostfully.technicalchallenge.service.booking.data.BookingStatus;
import com.hostfully.technicalchallenge.service.property.data.Property;
import com.hostfully.technicalchallenge.service.property.data.PropertyRepository;
import com.hostfully.technicalchallenge.service.property.data.block.PropertyBlock;
import com.hostfully.technicalchallenge.service.property.data.block.PropertyBlockRepository;
import com.hostfully.technicalchallenge.service.user.data.User;
import com.hostfully.technicalchallenge.service.user.data.UserRepository;
import com.hostfully.technicalchallenge.util.RandomEntityGenerator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

  @Mock
  private PropertyRepository propertyRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PropertyBlockRepository propertyBlockRepository;
  @Mock
  private BookingRepository bookingRepository;
  @Spy
  private PropertyMapper propertyMapper = new PropertyMapperImpl();
  @InjectMocks
  private PropertyServiceImpl propertyService;

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenRetrieveProperty() {
    assertThatThrownBy(() -> propertyService.retrieveProperty(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfPropertyDoesntExistWhenRetrieveProperty() {
    doReturn(Optional.empty()).when(propertyRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> propertyService.retrieveProperty(UUID.randomUUID()))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldRetrieveProperty() {
    final UUID propertyId = UUID.randomUUID();

    final Property property = RandomEntityGenerator.create(Property.class)
        .withId(propertyId)
        .withPricePerGuest(10000L);

    doReturn(Optional.of(property)).when(propertyRepository).findById(any(UUID.class));

    final PropertyDto retrieved = propertyService.retrieveProperty(propertyId);
    final PropertyDto expected = propertyMapper.entityToDto(property);

    assertThat(retrieved).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyInfoIsNullWhenCreateProperty() {
    assertThatThrownBy(() -> propertyService.createProperty(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfNameIsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withName(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfOwnerUserIdIsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withOwnerUserId(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfAddressLine1IsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withAddressLine1(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfCityIsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withCity(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfStateIsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withState(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfCountryIsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withCountry(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPostalCodeIsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPostalCode(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPricePerGuestIsNullWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(null);

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfOwnerUserDoesntExistWhenCreateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(10000L);

    doReturn(Optional.empty()).when(userRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> propertyService.createProperty(propertyInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldCreateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(10000L);

    final User user = RandomEntityGenerator.create(User.class);

    doReturn(Optional.of(user)).when(userRepository).findById(any(UUID.class));

    doAnswer(invocation -> {

      final Property propertyToBeSaved = invocation.getArgument(0, Property.class);
      return propertyToBeSaved.withId(propertyId);

    }).when(propertyRepository).save(any(Property.class));

    final PropertyDto saved = propertyService.createProperty(propertyInfo);
    final PropertyDto expected = propertyInfo.withId(propertyId);

    assertThat(saved).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenUpdateProperty() {
    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(null, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyInfoIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfNameIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withName(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfOwnerUserIdIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withOwnerUserId(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfAddressLine1IsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withAddressLine1(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfCityIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withCity(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfStateIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withState(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfCountryIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withCountry(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPostalCodeIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPostalCode(null)
        .withPricePerGuest(10000L);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPricePerGuestIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(null);

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfPropertyDoesntExistWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(10000L);

    doReturn(Optional.empty()).when(propertyRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfOwnerChangedAndUserDoesntExistWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(10000L);

    final Property property = RandomEntityGenerator.create(Property.class)
            .withId(propertyId);

    doReturn(Optional.of(property)).when(propertyRepository).findById(any(UUID.class));
    doReturn(Optional.empty()).when(userRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> propertyService.updateProperty(propertyId, propertyInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto propertyInfo = RandomEntityGenerator.create(PropertyDto.class)
        .withPricePerGuest(10000L);

    final Property retrievedProperty = RandomEntityGenerator.create(Property.class)
        .withId(propertyId);

    final User user = RandomEntityGenerator.create(User.class)
        .withId(propertyInfo.getOwnerUserId());

    doReturn(Optional.of(user)).when(userRepository).findById(any(UUID.class));
    doReturn(Optional.of(retrievedProperty)).when(propertyRepository).findById(any(UUID.class));
    doAnswer(AdditionalAnswers.returnsFirstArg()).when(propertyRepository).save(any(Property.class));

    final PropertyDto updated = propertyService.updateProperty(propertyId, propertyInfo);
    final PropertyDto expected = propertyInfo.withId(propertyId);

    assertThat(updated).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenDeleteProperty() {
    assertThatThrownBy(() -> propertyService.deleteProperty(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldDeletePropertyAndPropertyBlocks() {
    final UUID propertyId = UUID.randomUUID();

    propertyService.deleteProperty(propertyId);

    verify(propertyBlockRepository).deleteByPropertyId(any(UUID.class));
    verify(propertyRepository).deleteById(any(UUID.class));
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenBlockPropertyDates() {
    assertThatThrownBy(() -> propertyService.blockPropertyDates(null, Collections.emptySet(), ""))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldDoNothingIfDatesToBlockIsNullWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    propertyService.blockPropertyDates(propertyId, null, "");

    verifyNoInteractions(propertyRepository);
  }

  @Test
  void shouldDoNothingIfDatesToBlockIsEmptyWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    propertyService.blockPropertyDates(propertyId, Collections.emptySet(), "");

    verifyNoInteractions(propertyRepository);
  }

  @Test
  void shouldThrowNotFoundExceptionIfPropertyDoesntExistWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    doReturn(Optional.empty()).when(propertyRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> propertyService.blockPropertyDates(propertyId, Set.of(LocalDate.now()), null))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldUpdateExistingPropertyBlockIfDateIsAlreadyBlockedWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final Property property = RandomEntityGenerator.create(Property.class)
            .withId(propertyId);

    final PropertyBlock propertyBlock = RandomEntityGenerator.create(PropertyBlock.class)
        .withPropertyId(propertyId)
        .withDate(LocalDate.now());

    doReturn(Optional.of(property)).when(propertyRepository).findById(any(UUID.class));

    doReturn(Optional.of(propertyBlock)).when(propertyBlockRepository)
        .findByPropertyIdAndDate(any(UUID.class), any(LocalDate.class));

    propertyService.blockPropertyDates(propertyId, Set.of(LocalDate.now()), "new reason");

    verify(propertyBlockRepository).save(propertyBlock.withReason("new reason"));
  }

  @Test
  void shouldThrowDatesConflictExceptionIfDateHasBookingsWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final Property property = RandomEntityGenerator.create(Property.class)
        .withId(propertyId);

    final Booking booking = RandomEntityGenerator.create(Booking.class)
        .withPropertyId(propertyId)
        .withStatus(BookingStatus.BOOKED)
        .withStartDate(LocalDate.now().minusDays(1L))
        .withEndDate(LocalDate.now().plusDays(1L));

    doReturn(Optional.of(property)).when(propertyRepository).findById(any(UUID.class));
    doReturn(Optional.empty()).when(propertyBlockRepository).findByPropertyIdAndDate(any(UUID.class), any(LocalDate.class));
    doReturn(List.of(booking)).when(bookingRepository).findNonCanceledBookingsOverlappingWithDates(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    assertThatThrownBy(() -> propertyService.blockPropertyDates(propertyId, Set.of(LocalDate.now()), null))
        .isInstanceOf(DatesConflictException.class);
  }

  @Test
  void shouldBlockPropertyDate() {
    final UUID propertyId = UUID.randomUUID();

    final Property property = RandomEntityGenerator.create(Property.class)
        .withId(propertyId);

    doReturn(Optional.of(property)).when(propertyRepository).findById(any(UUID.class));
    doReturn(Optional.empty()).when(propertyBlockRepository).findByPropertyIdAndDate(any(UUID.class), any(LocalDate.class));
    doReturn(Collections.emptyList()).when(bookingRepository).findNonCanceledBookingsOverlappingWithDates(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    propertyService.blockPropertyDates(propertyId, Set.of(LocalDate.now()), null);

    verify(propertyBlockRepository).save(any(PropertyBlock.class));
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenUnblockPropertyDates() {
    assertThatThrownBy(() -> propertyService.unblockPropertyDates(null, Collections.emptySet()))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldDoNothingIfDatesToUnblockIsNullWhenUnblockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    propertyService.unblockPropertyDates(propertyId, null);

    verifyNoInteractions(propertyBlockRepository);
  }

  @Test
  void shouldDoNothingIfDatesToUnblockIsEmptyWhenUnblockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    propertyService.unblockPropertyDates(propertyId, Collections.emptySet());

    verifyNoInteractions(propertyBlockRepository);
  }

  @Test
  void shouldUnblockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    propertyService.unblockPropertyDates(propertyId, Set.of(LocalDate.now()));

    verify(propertyBlockRepository).deleteByPropertyIdAndDateIn(any(UUID.class), any(ArrayList.class));
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenRetrievePropertyBlockedDates() {
    assertThatThrownBy(() ->
        propertyService.retrievePropertyBlockedDates(
            null,
            LocalDate.now(),
            LocalDate.now().plusDays(1)))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfStartDateIsNullWhenRetrievePropertyBlockedDates() {
    final UUID propertyId = UUID.randomUUID();

    assertThatThrownBy(() ->
        propertyService.retrievePropertyBlockedDates(
            propertyId,
            null,
            LocalDate.now().plusDays(1)))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfEndDateIsNullWhenRetrievePropertyBlockedDates() {
    final UUID propertyId = UUID.randomUUID();

    assertThatThrownBy(() ->
        propertyService.retrievePropertyBlockedDates(
            propertyId,
            LocalDate.now(),
            null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfStartDateIsAfterEndDate() {
    final UUID propertyId = UUID.randomUUID();

    assertThatThrownBy(() ->
        propertyService.retrievePropertyBlockedDates(
            propertyId,
            LocalDate.now().plusDays(1L),
            LocalDate.now()))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldRetrievePropertyBlockedDates() {
    final UUID propertyId = UUID.randomUUID();

    final List<PropertyBlock> propertyBlocks =
        RandomEntityGenerator.createList(PropertyBlock.class, 3)
            .stream()
            .map(block -> block.withPropertyId(propertyId))
            .toList();

    doReturn(propertyBlocks).when(propertyBlockRepository)
        .findByPropertyIdAndDateBetween(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    final List<PropertyBlockDto> retrieved =
        propertyService.retrievePropertyBlockedDates(
            propertyId,
            LocalDate.now(),
            LocalDate.now().plusDays(3L));

    final List<PropertyBlockDto> expected = propertyBlocks.stream()
        .map(propertyMapper::entityToDto)
        .toList();

    assertThat(retrieved).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfPropertyIdIsNullWhenRetrievePropertyUnavailableDatesForPeriod() {
    assertThatThrownBy(() ->
        propertyService.retrievePropertyUnavailableDatesForPeriod(
            null,
            LocalDate.now(),
            LocalDate.now().plusDays(1L)))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfStartDateIsNullWhenRetrievePropertyUnavailableDatesForPeriod() {
    final UUID propertyId = UUID.randomUUID();

    assertThatThrownBy(() ->
        propertyService.retrievePropertyUnavailableDatesForPeriod(
            propertyId,
            null,
            LocalDate.now().plusDays(1L)))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfEndDateIsNullWhenRetrievePropertyUnavailableDatesForPeriod() {
    final UUID propertyId = UUID.randomUUID();

    assertThatThrownBy(() ->
        propertyService.retrievePropertyUnavailableDatesForPeriod(
            propertyId,
            LocalDate.now(),
            null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldRetrievePropertyUnavailableDatesForPeriod() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyBlock propertyBlock = RandomEntityGenerator.create(PropertyBlock.class)
        .withDate(LocalDate.now())
        .withPropertyId(propertyId);

    final Booking booking = RandomEntityGenerator.create(Booking.class)
        .withPropertyId(propertyId)
        .withStartDate(LocalDate.now().plusDays(1L))
        .withEndDate(LocalDate.now().plusDays(1L))
        .withStatus(BookingStatus.BOOKED);

    doReturn(List.of(propertyBlock)).when(propertyBlockRepository)
        .findByPropertyIdAndDateBetween(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    doReturn(List.of(booking)).when(bookingRepository)
        .findNonCanceledBookingsOverlappingWithDates(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    final List<LocalDate> retrieved =
        propertyService.retrievePropertyUnavailableDatesForPeriod(
            propertyId,
            LocalDate.now(),
            LocalDate.now().plusDays(2L));

    final List<LocalDate> expected = List.of(LocalDate.now(), LocalDate.now().plusDays(1L));

    assertThat(retrieved).hasSameElementsAs(expected);
  }
}
