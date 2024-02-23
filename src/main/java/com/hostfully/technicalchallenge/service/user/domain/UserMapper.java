package com.hostfully.technicalchallenge.service.user.domain;

import com.hostfully.technicalchallenge.service.user.api.request.UpsertUserRequest;
import com.hostfully.technicalchallenge.service.user.api.response.UserResponse;
import com.hostfully.technicalchallenge.service.user.data.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

  UserResponse dtoToResponse(final UserDto dto);
  UserDto upsertRequestToDto(final UpsertUserRequest upsertRequest);

  UserDto entityToDto(final User entity);

  User dtoToEntity(final UserDto dto);
}
