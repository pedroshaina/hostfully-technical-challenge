package com.hostfully.technicalchallenge.service.property.domain;

import com.hostfully.technicalchallenge.service.property.api.request.UpsertPropertyRequest;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyBlockResponse;
import com.hostfully.technicalchallenge.service.property.api.response.PropertyResponse;
import com.hostfully.technicalchallenge.service.property.data.Property;
import com.hostfully.technicalchallenge.service.property.data.block.PropertyBlock;
import org.mapstruct.Mapper;

@Mapper
public interface PropertyMapper {
  PropertyResponse dtoToResponse(final PropertyDto dto);
  PropertyDto upsertRequestToDto(final UpsertPropertyRequest upsertRequest);

  PropertyDto entityToDto(final Property entity);

  Property dtoToEntity(final PropertyDto dto);

  PropertyBlockDto entityToDto(final PropertyBlock entity);
  PropertyBlockResponse dtoToResponse(final PropertyBlockDto dto);
}
