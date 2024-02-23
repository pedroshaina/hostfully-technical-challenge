package com.hostfully.technicalchallenge.service.property.api;

import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyBlockRequest;
import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyRequest;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyBlockResponse;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Property API")
@RequestMapping(value = PropertyApi.PROPERTY_API_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public interface PropertyApi {
  String PROPERTY_API_PATH = "/properties";

  @Operation(summary = "Retrieve a property by its ID")
  @ApiResponse(
      responseCode = "200",
      description = "The retrieved property",
      useReturnTypeSchema = true
  )
  @GetMapping(value = "/{id}")
  PropertyResponse retrieveProperty(@PathVariable("id") final UUID propertyId);

  @Operation(summary = "Creates a property")
  @ApiResponse(
      responseCode = "201",
      description = "The created property",
      useReturnTypeSchema = true
  )
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  PropertyResponse createProperty(@RequestBody @Validated final UpsertPropertyRequest propertyInfo);

  @Operation(summary = "Updates a property by its ID")
  @ApiResponse(
      responseCode = "200",
      description = "The updated property",
      useReturnTypeSchema = true
  )
  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  PropertyResponse updateProperty(
      @PathVariable("id") final UUID propertyId,
      @RequestBody @Validated final UpsertPropertyRequest propertyInfo);

  @Operation(summary = "Deletes a property by its ID")
  @ApiResponse(
      responseCode = "204",
      description = "The property was deleted"
  )
  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteProperty(@PathVariable("id") final UUID propertyId);

  @Operation(summary = "Blocks a list of dates of a property. If a date is already blocked, the reason will be updated")
  @ApiResponse(
      responseCode = "204",
      description = "The dates were blocked"
  )
  @PostMapping(value = "/{id}/block")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void blockPropertyDates(
      @PathVariable("id") final UUID propertyId,
      @RequestBody @Validated final UpsertPropertyBlockRequest propertyBlockInfo);

  @Operation(summary = "Unblocks a list of dates of a property")
  @ApiResponse(
      responseCode = "204",
      description = "The dates were unblocked"
  )
  @PostMapping(value = "/{id}/unblock")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void unblockPropertyDates(
      @PathVariable("id") final UUID propertyId,
      @RequestBody @Validated final UpsertPropertyBlockRequest propertyBlockInfo);

  @Operation(summary = "Retrieves the list of blocked dates of a property for a given date range")
  @ApiResponse(
      responseCode = "200",
      description = "The blocked dates of a property",
      useReturnTypeSchema = true
  )
  @GetMapping(value = "/{id}/blocks")
  List<PropertyBlockResponse> retrievePropertyBlockedDates(
      @PathVariable("id") final UUID propertyId,
      @RequestParam("startDate") final LocalDate startDate,
      @RequestParam("endDate") final LocalDate endDate);
}
