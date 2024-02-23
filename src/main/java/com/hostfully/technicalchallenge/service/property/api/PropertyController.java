package com.hostfully.technicalchallenge.service.property.api;


import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyBlockRequest;
import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyRequest;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyBlockResponse;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyResponse;
import com.hostfully.technicalchallenge.service.property.domain.PropertyBlockDto;
import com.hostfully.technicalchallenge.service.property.domain.PropertyDto;
import com.hostfully.technicalchallenge.service.property.domain.PropertyMapper;
import com.hostfully.technicalchallenge.service.property.domain.PropertyService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PropertyController implements PropertyApi {

  private final PropertyService propertyService;
  private final PropertyMapper propertyMapper;

  @Override
  public PropertyResponse retrieveProperty(final UUID propertyId) {
    final PropertyDto retrieved = propertyService.retrieveProperty(propertyId);
    return propertyMapper.dtoToResponse(retrieved);
  }

  @Override
  public PropertyResponse createProperty(final UpsertPropertyRequest propertyInfo) {
    final PropertyDto toBeSaved = propertyMapper.upsertRequestToDto(propertyInfo);
    final PropertyDto saved = propertyService.createProperty(toBeSaved);
    return propertyMapper.dtoToResponse(saved);
  }

  @Override
  public PropertyResponse updateProperty(final UUID propertyId, final UpsertPropertyRequest propertyInfo) {
    final PropertyDto toBeUpdated = propertyMapper.upsertRequestToDto(propertyInfo);
    final PropertyDto updated = propertyService.updateProperty(propertyId, toBeUpdated);
    return propertyMapper.dtoToResponse(updated);
  }

  @Override
  public void deleteProperty(final UUID propertyId) {
    propertyService.deleteProperty(propertyId);
  }

  @Override
  public void blockPropertyDates(
      final UUID propertyId,
      final UpsertPropertyBlockRequest propertyBlockInfo) {

    propertyService.blockPropertyDates(
        propertyId,
        propertyBlockInfo.getDates(),
        propertyBlockInfo.getReason());
  }

  @Override
  public void unblockPropertyDates(
      final UUID propertyId,
      final UpsertPropertyBlockRequest propertyBlockInfo) {

    propertyService.unblockPropertyDates(propertyId, propertyBlockInfo.getDates());
  }

  @Override
  public List<PropertyBlockResponse> retrievePropertyBlockedDates(
      final UUID propertyId,
      final LocalDate startDate,
      final LocalDate endDate) {

    final List<PropertyBlockDto> blocks =
        propertyService.retrievePropertyBlockedDates(propertyId, startDate, endDate);

    return blocks.stream()
        .map(propertyMapper::dtoToResponse)
        .toList();
  }
}
