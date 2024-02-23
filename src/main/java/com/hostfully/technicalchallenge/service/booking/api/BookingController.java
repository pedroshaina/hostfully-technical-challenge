package com.hostfully.technicalchallenge.service.booking.api;


import com.hostfully.technicalchallenge.service.booking.api.request.CreateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.request.UpdateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.response.BookingResponse;
import com.hostfully.technicalchallenge.service.booking.domain.BookingDto;
import com.hostfully.technicalchallenge.service.booking.domain.BookingMapper;
import com.hostfully.technicalchallenge.service.booking.domain.BookingService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookingController implements BookingApi {

  private final BookingService bookingService;
  private final BookingMapper bookingMapper;

  @Override
  public BookingResponse retrieveBooking(final UUID bookingId) {
    final BookingDto retrieved = bookingService.retrieveBooking(bookingId);
    return bookingMapper.dtoToResponse(retrieved);
  }

  @Override
  public BookingResponse createBooking(final CreateBookingRequest bookingInfo) {
    final BookingDto toBeSaved = bookingMapper.createRequestToDto(bookingInfo);
    final BookingDto saved = bookingService.createBooking(toBeSaved);
    return bookingMapper.dtoToResponse(saved);
  }

  @Override
  public BookingResponse updateBooking(
      final UUID bookingId,
      final UpdateBookingRequest bookingInfo) {

    final BookingDto toBeUpdated = bookingMapper.updateRequestToDto(bookingInfo);
    final BookingDto updated = bookingService.updateBooking(bookingId, toBeUpdated);
    return bookingMapper.dtoToResponse(updated);
  }

  @Override
  public BookingResponse cancelBooking(final UUID bookingId) {
    final BookingDto canceled = bookingService.cancelBooking(bookingId);
    return bookingMapper.dtoToResponse(canceled);
  }

  @Override
  public BookingResponse rebookBooking(final UUID bookingId) {
    final BookingDto rebooked = bookingService.rebookCanceledBooking(bookingId);
    return bookingMapper.dtoToResponse(rebooked);
  }

  @Override
  public void deleteBooking(final UUID bookingId) {
    bookingService.deleteBooking(bookingId);
  }
}
