package com.hostfully.technicalchallenge.service.user.api;

import com.hostfully.technicalchallenge.service.user.api.request.UpsertUserRequest;
import com.hostfully.technicalchallenge.service.user.api.response.UserResponse;
import com.hostfully.technicalchallenge.service.user.domain.UserDto;
import com.hostfully.technicalchallenge.service.user.domain.UserMapper;
import com.hostfully.technicalchallenge.service.user.domain.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  public UserResponse retrieveUser(final UUID userId) {
    final UserDto retrieved = userService.retrieveUser(userId);
    return userMapper.dtoToResponse(retrieved);
  }

  @Override
  public UserResponse createUser(final UpsertUserRequest userInfo) {
    final UserDto toBeSaved = userMapper.upsertRequestToDto(userInfo);
    final UserDto saved = userService.createUser(toBeSaved);
    return userMapper.dtoToResponse(saved);
  }

  @Override
  public UserResponse updateUser(final UUID userId, final UpsertUserRequest userInfo) {
    final UserDto toBeUpdated = userMapper.upsertRequestToDto(userInfo);
    final UserDto updated = userService.updateUser(userId, toBeUpdated);
    return userMapper.dtoToResponse(updated);
  }

  @Override
  public void deleteUser(final UUID userId) {
    userService.deleteUser(userId);
  }
}
