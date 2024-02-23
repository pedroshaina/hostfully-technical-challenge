package com.hostfully.technicalchallenge.service.property.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyBlockRequest;
import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyRequest;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyBlockResponse;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyResponse;
import com.hostfully.technicalchallenge.service.property.domain.PropertyBlockDto;
import com.hostfully.technicalchallenge.service.property.domain.PropertyDto;
import com.hostfully.technicalchallenge.service.property.domain.PropertyMapper;
import com.hostfully.technicalchallenge.service.property.domain.PropertyMapperImpl;
import com.hostfully.technicalchallenge.service.property.domain.PropertyService;
import com.hostfully.technicalchallenge.util.RandomEntityGenerator;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@Import({PropertyMapperImpl.class})
@WebMvcTest(controllers = {PropertyController.class})
class PropertyApiTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private PropertyMapper propertyMapper;
  @MockBean
  private PropertyService propertyService;

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenGetPropertyById() {
    mockMvc
        .perform(get(PropertyApi.PROPERTY_API_PATH + "/{id}", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfPropertyDoesntExistWhenGetPropertyById() {
    final UUID propertyId = UUID.randomUUID();

    doThrow(new NotFoundException(String.format("No property found with id %s", propertyId)))
        .when(propertyService).retrieveProperty(any(UUID.class));

    mockMvc
        .perform(get(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithPropertyWhenGetPropertyById() {
    final UUID propertyId = UUID.randomUUID();

    final PropertyDto retrievedProperty = RandomEntityGenerator.create(PropertyDto.class)
        .withId(propertyId);

    final PropertyResponse expectedResponse = propertyMapper.dtoToResponse(retrievedProperty);

    doReturn(retrievedProperty).when(propertyService).retrieveProperty(any(UUID.class));

    mockMvc
        .perform(get(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidJsonWhenCreateProperty() {
    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfNameIsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withName(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfOwnerUserIdIsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withOwnerUserId(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfAddressLine1IsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withAddressLine1(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfCityIsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withCity(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfStateIsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withState(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfCountryIsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withCountry(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPostalCodeIsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPostalCode(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPricePerGuestIsNullWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(null);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPricePerGuestIsNegativeWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(-10000L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPricePerGuestIsZeroWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(0L);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfOwnerUserDoesntExistWhenCreateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(10000L);

    doThrow(new NotFoundException(String.format("No user found with provided ownerUserId of '%s'", request.getOwnerUserId())))
        .when(propertyService).createProperty(any(PropertyDto.class));

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn201WithCreatedPropertyWhenCreateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(10000L);

    doAnswer(invocation -> {

      final PropertyDto propertyInfo = invocation.getArgument(0, PropertyDto.class);
      return propertyInfo.withId(propertyId);

    }).when(propertyService).createProperty(any(PropertyDto.class));

    final PropertyResponse expectedResponse =
        propertyMapper.dtoToResponse(
            propertyMapper.upsertRequestToDto(request).withId(propertyId));

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenUpdateProperty() {
    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidJsonIsProvidedWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfNameIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withName(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfOwnerUserIdIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withOwnerUserId(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfAddressLine1IsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withAddressLine1(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfCityIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withCity(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfStateIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withState(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfCountryIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withCountry(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPostalCodeIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPostalCode(null)
        .withPricePerGuest(10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPricePerGuestIsNullWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(null);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPricePerGuestIsNegativeWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(-10000L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfPricePerGuestIsZeroWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(0L);

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfPropertyDoesntExistWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(10000L);

    doThrow(new NotFoundException(String.format("No property found with id %s", propertyId)))
        .when(propertyService).updateProperty(any(UUID.class), any(PropertyDto.class));

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfOwnerUserDoesntExistWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(10000L);

    doThrow(new NotFoundException(String.format("No user found with provided ownerUserId of '%s'", request.getOwnerUserId())))
        .when(propertyService).updateProperty(any(UUID.class), any(PropertyDto.class));

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithUpdatedPropertyWhenUpdateProperty() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyRequest request = RandomEntityGenerator.create(UpsertPropertyRequest.class)
        .withPricePerGuest(10000L);

    final PropertyDto persisted = propertyMapper.upsertRequestToDto(request)
        .withId(propertyId);

    final PropertyResponse expectedResponse = propertyMapper.dtoToResponse(persisted);

    doReturn(persisted).when(propertyService).updateProperty(any(UUID.class), any(PropertyDto.class));

    mockMvc
        .perform(
            put(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenDeleteProperty() {
    mockMvc
        .perform(delete(PropertyApi.PROPERTY_API_PATH + "/{id}", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn204WhenDeleteProperty() {
    final UUID propertyId = UUID.randomUUID();

    mockMvc
        .perform(delete(PropertyApi.PROPERTY_API_PATH + "/{id}", propertyId))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenBlockPropertyDates() {
    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/block", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDatesIsNullWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class)
            .withDates(null);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/block", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn204IfDatesIsEmptyWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class)
            .withDates(Collections.emptySet());

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/block", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(propertyService).blockPropertyDates(any(UUID.class), any(Set.class), anyString());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfPropertyDoesntExistWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class);

    doThrow(new NotFoundException(String.format("No property found with id %s", propertyId)))
        .when(propertyService).blockPropertyDates(any(UUID.class), any(Set.class), anyString());

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/block", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDatesAreBookedWhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class);

    doThrow(new DatesConflictException("Cannot block dates that overlaps with active bookings"))
        .when(propertyService).blockPropertyDates(any(UUID.class), any(Set.class), anyString());

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/block", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn204WhenBlockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/block", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(propertyService).blockPropertyDates(any(UUID.class), any(Set.class), anyString());
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenUnblockPropertyDates() {
    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class)
            .withReason(null);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/unblock", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDatesIsNullWhenUnblockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class)
            .withDates(null);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/unblock", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn204IfDatesIsEmptyWhenUnblockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class)
            .withDates(Collections.emptySet());

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/unblock", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(propertyService).unblockPropertyDates(any(UUID.class), any(Set.class));
  }

  @Test
  @SneakyThrows
  void shouldReturn204WhenUnblockPropertyDates() {
    final UUID propertyId = UUID.randomUUID();

    final UpsertPropertyBlockRequest request =
        RandomEntityGenerator.create(UpsertPropertyBlockRequest.class);

    mockMvc
        .perform(
            post(PropertyApi.PROPERTY_API_PATH + "/{id}/unblock", propertyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNoContent());

    verify(propertyService).unblockPropertyDates(any(UUID.class), any(Set.class));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenRetrievePropertyBlockedDates() {
    mockMvc
        .perform(
            get(PropertyApi.PROPERTY_API_PATH + "/{id}/blocks", "invalid-uuid")
                .queryParam("startDate", LocalDate.now().minusDays(1L).toString())
                .queryParam("endDate", LocalDate.now().plusDays(1L).toString()))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidStartDateIsProvidedWhenRetrievePropertyBlockedDates() {
    final UUID propertyId = UUID.randomUUID();

    mockMvc
        .perform(
            get(PropertyApi.PROPERTY_API_PATH + "/{id}/blocks", propertyId)
                .queryParam("startDate", "invalid-date")
                .queryParam("endDate", LocalDate.now().plusDays(1L).toString()))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidEndDateIsProvidedWhenRetrievePropertyBlockedDates() {
    final UUID propertyId = UUID.randomUUID();

    mockMvc
        .perform(
            get(PropertyApi.PROPERTY_API_PATH + "/{id}/blocks", propertyId)
                .queryParam("startDate", LocalDate.now().minusDays(1L).toString())
                .queryParam("endDate", "invalid-date"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfStartDateIsAfterEndDateWhenRetrievePropertyBlockedDates() {
    final UUID propertyId = UUID.randomUUID();

    doThrow(new IllegalArgumentException("startDate cannot be after endDate"))
        .when(propertyService).retrievePropertyBlockedDates(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    mockMvc
        .perform(
            get(PropertyApi.PROPERTY_API_PATH + "/{id}/blocks", propertyId)
                .queryParam("startDate", LocalDate.now().plusDays(1L).toString())
                .queryParam("endDate", LocalDate.now().minusDays(1L).toString()))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithBlockedDatesWhenRetrievePropertyBlockedDates() {
    final UUID propertyId = UUID.randomUUID();

    final List<PropertyBlockDto> retrievedBlocks = RandomEntityGenerator.createList(PropertyBlockDto.class, 3)
        .stream()
        .map(block -> block.withPropertyId(propertyId))
        .toList();

    final List<PropertyBlockResponse> expectedResponse = retrievedBlocks.stream()
        .map(propertyMapper::dtoToResponse)
        .toList();

    doReturn(retrievedBlocks).when(propertyService)
        .retrievePropertyBlockedDates(any(UUID.class), any(LocalDate.class), any(LocalDate.class));

    mockMvc
        .perform(
            get(PropertyApi.PROPERTY_API_PATH + "/{id}/blocks", propertyId)
                .queryParam("startDate", LocalDate.now().minusDays(1L).toString())
                .queryParam("endDate", LocalDate.now().plusDays(1L).toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }
}
