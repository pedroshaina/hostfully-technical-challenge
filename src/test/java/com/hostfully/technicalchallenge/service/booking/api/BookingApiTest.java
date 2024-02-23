package com.hostfully.technicalchallenge.service.booking.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.technicalchallenge.common.exception.DatesConflictException;
import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import com.hostfully.technicalchallenge.service.booking.api.request.CreateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.request.UpdateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.request.UpsertBookingGuestRequest;
import com.hostfully.technicalchallenge.service.booking.api.response.BookingResponse;
import com.hostfully.technicalchallenge.service.booking.data.BookingStatus;
import com.hostfully.technicalchallenge.service.booking.domain.BookingDto;
import com.hostfully.technicalchallenge.service.booking.domain.BookingGuestDto;
import com.hostfully.technicalchallenge.service.booking.domain.BookingMapper;
import com.hostfully.technicalchallenge.service.booking.domain.BookingMapperImpl;
import com.hostfully.technicalchallenge.service.booking.domain.BookingService;
import com.hostfully.technicalchallenge.service.property.api.PropertyApi;
import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyRequest;
import com.hostfully.technicalchallenge.util.RandomEntityGenerator;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Import({BookingMapperImpl.class})
@WebMvcTest(controllers = {BookingController.class})
class BookingApiTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private BookingMapper bookingMapper;
  @MockBean
  private BookingService bookingService;

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenGetBookingById() {
    mockMvc
        .perform(get(BookingApi.BOOKING_API_PATH + "/{id}", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfBookingDoesntExistWhenGetBookingById() {
    final UUID bookingId = UUID.randomUUID();

    doThrow(new NotFoundException(String.format("No booking found with id %s", bookingId)))
        .when(bookingService).retrieveBooking(any(UUID.class));

    mockMvc
        .perform(get(BookingApi.BOOKING_API_PATH + "/{id}", bookingId))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithBookingWhenGetBookingById() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto retrievedBooking = RandomEntityGenerator.create(BookingDto.class)
        .withId(bookingId);

    final BookingResponse expectedResponse = bookingMapper.dtoToResponse(retrievedBooking);

    doReturn(retrievedBooking).when(bookingService).retrieveBooking(any(UUID.class));

    mockMvc
        .perform(get(BookingApi.BOOKING_API_PATH + "/{id}", bookingId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidJsonWhenCreateBooking() {
    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPropertyIdIsNullWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class)
        .withPropertyId(null);

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfUserIdIsNullWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class)
        .withUserId(null);

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfStartDateIsNullWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class)
        .withStartDate(null);

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfEndDateIsNullWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class)
        .withEndDate(null);

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfGuestsIsNullWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class)
        .withGuests(null);

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfGuestsIsEmptyWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class)
        .withGuests(Collections.emptyList());

    doThrow(new IllegalArgumentException("Cannot create booking without guests information"))
        .when(bookingService).createBooking(any(BookingDto.class));

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfUserDoesntExistWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class);

    doThrow(new NotFoundException(String.format("No user found with provided userId %s", request.getUserId())))
        .when(bookingService).createBooking(any(BookingDto.class));

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfPropertyDoesntExistWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class);

    doThrow(new NotFoundException(String.format("No property found with provided propertyId %s", request.getPropertyId())))
        .when(bookingService).createBooking(any(BookingDto.class));

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDatesAreUnavailableWhenCreateBooking() {
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class);

    doThrow(new DatesConflictException("The booking dates contains unavailable dates for the selected property"))
        .when(bookingService).createBooking(any(BookingDto.class));

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn201WithCreatedBookingWhenCreateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpsertBookingGuestRequest guestRequest = RandomEntityGenerator.create(UpsertBookingGuestRequest.class);
    final CreateBookingRequest request = RandomEntityGenerator.create(CreateBookingRequest.class)
        .withGuests(List.of(guestRequest));

    doAnswer(invocation -> {

      final BookingDto bookingInfo = invocation.getArgument(0, BookingDto.class);
      final BookingGuestDto bookingGuestDto = bookingMapper.upsertRequestToDto(guestRequest);
      return bookingInfo.withId(bookingId).withGuests(List.of(bookingGuestDto));

    }).when(bookingService).createBooking(any(BookingDto.class));

    final BookingResponse expectedResponse = bookingMapper.dtoToResponse(
        bookingMapper.createRequestToDto(request).withId(bookingId));

    mockMvc
        .perform(
            post(BookingApi.BOOKING_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenUpdateBooking() {
    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class);

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidJsonIsProvidedWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfStartDateIsNullWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class)
        .withStartDate(null);

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfEndDateIsNullWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class)
        .withEndDate(null);

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfGuestsIsNullWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class)
        .withGuests(null);

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfGuestsIsEmptyWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class)
        .withGuests(Collections.emptyList());

    doThrow(new IllegalArgumentException("Cannot update booking without guests information"))
        .when(bookingService).updateBooking(any(UUID.class), any(BookingDto.class));

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfBookingDoesntExistWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class);

    doThrow(new NotFoundException(String.format("No booking found with id %s", bookingId)))
        .when(bookingService).updateBooking(any(UUID.class), any(BookingDto.class));

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfBookingIsCanceledWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class);

    doThrow(new IllegalStateException("Cannot update canceled booking"))
        .when(bookingService).updateBooking(any(UUID.class), any(BookingDto.class));

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfPropertyDoesntExistWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class);

    doThrow(new NotFoundException(String.format("No property found with provided propertyId %s", UUID.randomUUID())))
        .when(bookingService).updateBooking(any(UUID.class), any(BookingDto.class));

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDatesAreUnavailableWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class);

    doThrow(new DatesConflictException("The booking dates contains unavailable dates for the selected property"))
        .when(bookingService).updateBooking(any(UUID.class), any(BookingDto.class));

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithUpdatedBookingWhenUpdateBooking() {
    final UUID bookingId = UUID.randomUUID();

    final UpsertBookingGuestRequest bookingGuestRequest = RandomEntityGenerator.create(UpsertBookingGuestRequest.class);
    final UpdateBookingRequest request = RandomEntityGenerator.create(UpdateBookingRequest.class)
        .withGuests(List.of(bookingGuestRequest));

    final BookingDto persisted = bookingMapper.updateRequestToDto(request)
        .withId(bookingId)
        .withStartDate(request.getStartDate())
        .withEndDate(request.getEndDate())
        .withGuests(List.of(bookingMapper.upsertRequestToDto(bookingGuestRequest)));

    final BookingResponse expectedResponse = bookingMapper.dtoToResponse(persisted);

    doReturn(persisted).when(bookingService).updateBooking(any(UUID.class), any(BookingDto.class));

    mockMvc
        .perform(
            put(BookingApi.BOOKING_API_PATH + "/{id}", bookingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenCancelBooking() {
    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/cancel", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfBookingDoesntExistWhenCancelBooking() {
    final UUID bookingId = UUID.randomUUID();

    doThrow(new NotFoundException(String.format("No booking found with id %s", bookingId)))
        .when(bookingService).cancelBooking(any(UUID.class));

    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/cancel", bookingId))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfBookingIsAlreadyCanceledWhenCancelBooking() {
    final UUID bookingId = UUID.randomUUID();

    doThrow(new IllegalStateException("Cannot cancel an already canceled booking"))
        .when(bookingService).cancelBooking(any(UUID.class));

    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/cancel", bookingId))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithCanceledBookingWhenCancelBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto booking = RandomEntityGenerator.create(BookingDto.class)
            .withStatus(BookingStatus.CANCELED);

    final BookingResponse expectedResponse = bookingMapper.dtoToResponse(booking);

    doReturn(booking).when(bookingService).cancelBooking(any(UUID.class));

    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/cancel", bookingId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenRebookCanceledBooking() {
    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/rebook", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfBookingDoesntExistWhenRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    doThrow(new NotFoundException(String.format("No booking found with id %s", bookingId)))
        .when(bookingService).rebookCanceledBooking(any(UUID.class));

    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/rebook", bookingId))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfBookingIsAlreadyCanceledWhenRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    doThrow(new IllegalStateException("Can only rebook a canceled booking"))
        .when(bookingService).rebookCanceledBooking(any(UUID.class));

    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/rebook", bookingId))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDatesAreUnavailableWhenRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    doThrow(new DatesConflictException("The booking dates contains unavailable dates for the selected property"))
        .when(bookingService).rebookCanceledBooking(any(UUID.class));

    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/rebook", bookingId))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithRebookedBookingWhenRebookCanceledBooking() {
    final UUID bookingId = UUID.randomUUID();

    final BookingDto booking = RandomEntityGenerator.create(BookingDto.class)
        .withStatus(BookingStatus.BOOKED);

    final BookingResponse expectedResponse = bookingMapper.dtoToResponse(booking);

    doReturn(booking).when(bookingService).rebookCanceledBooking(any(UUID.class));

    mockMvc
        .perform(post(BookingApi.BOOKING_API_PATH + "/{id}/rebook", bookingId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenDeleteBooking() {
    mockMvc
        .perform(delete(BookingApi.BOOKING_API_PATH + "/{id}", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn204WhenDeleteBooking() {
    final UUID bookingId = UUID.randomUUID();

    mockMvc
        .perform(delete(BookingApi.BOOKING_API_PATH + "/{id}", bookingId))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(bookingService).deleteBooking(any(UUID.class));
  }
}
