package com.hostfully.technicalchallenge.service.booking.data.guest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Entity
@Table(name = "booking_guests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder
public class BookingGuest {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID bookingId;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private LocalDate dateOfBirth;
}
