package com.hostfully.technicalchallenge.service.booking.api;

import com.hostfully.technicalchallenge.service.booking.api.request.CreateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.request.UpdateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.response.BookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Booking API")
@RequestMapping(value = BookingApi.BOOKING_API_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public interface BookingApi {
  String BOOKING_API_PATH = "/bookings";

  @Operation(summary = "Retrieves a booking by its ID")
  @ApiResponse(
      responseCode = "200",
      description = "The retrieved booking",
      useReturnTypeSchema = true
  )
  @GetMapping(value = "/{id}")
  BookingResponse retrieveBooking(@PathVariable("id") final UUID bookingId);

  @Operation(summary = "Creates a new booking")
  @ApiResponse(
      responseCode = "201",
      description = "The created booking",
      useReturnTypeSchema = true
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  BookingResponse createBooking(@RequestBody @Validated final CreateBookingRequest bookingInfo);

  @Operation(summary = "Updates an existing booking")
  @ApiResponse(
      responseCode = "200",
      description = "The updated booking",
      useReturnTypeSchema = true
  )
  @PutMapping(value = "/{id}")
  BookingResponse updateBooking(
      @PathVariable("id") final UUID bookingId,
      @RequestBody @Validated final UpdateBookingRequest bookingInfo);

  @Operation(summary = "Cancels an existing booking")
  @ApiResponse(
      responseCode = "200",
      description = "The canceled booking",
      useReturnTypeSchema = true
  )
  @PostMapping(value = "/{id}/cancel")
  BookingResponse cancelBooking(@PathVariable("id") final UUID bookingId);

  @Operation(summary = "Rebooks a canceled booking")
  @ApiResponse(
      responseCode = "200",
      description = "The rebooked booking",
      useReturnTypeSchema = true
  )
  @PostMapping(value = "/{id}/rebook")
  BookingResponse rebookBooking(@PathVariable("id") final UUID bookingId);

  @Operation(summary = "Deletes an existing booking")
  @ApiResponse(
      responseCode = "204",
      description = "The booking was deleted"
  )
  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteBooking(@PathVariable("id") final UUID bookingId);
}
