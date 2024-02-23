package com.hostfully.technicalchallenge.service.booking.domain;

import com.hostfully.technicalchallenge.service.booking.api.request.CreateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.request.UpdateBookingRequest;
import com.hostfully.technicalchallenge.service.booking.api.request.UpsertBookingGuestRequest;
import com.hostfully.technicalchallenge.service.booking.api.response.BookingGuestResponse;
import com.hostfully.technicalchallenge.service.booking.api.response.BookingResponse;
import com.hostfully.technicalchallenge.service.booking.data.Booking;
import com.hostfully.technicalchallenge.service.booking.data.guest.BookingGuest;
import org.mapstruct.Mapper;

@Mapper
public interface BookingMapper {
  BookingResponse dtoToResponse(final BookingDto dto);
  BookingDto createRequestToDto(final CreateBookingRequest createRequest);

  BookingDto updateRequestToDto(final UpdateBookingRequest updateRequest);

  BookingDto entityToDto(final Booking entity);

  Booking dtoToEntity(final BookingDto dto);

  BookingGuestDto entityToDto(final BookingGuest entity);
  BookingGuestDto upsertRequestToDto(final UpsertBookingGuestRequest upsertRequest);
  BookingGuestResponse dtoToResponse(final BookingGuestDto dto);
}
