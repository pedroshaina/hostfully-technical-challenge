package com.hostfully.technicalchallenge.service.user.domain;

import java.util.UUID;

public interface UserService {
  UserDto retrieveUser(final UUID userId);
  UserDto createUser(final UserDto userInfo);
  UserDto updateUser(final UUID userId, final UserDto userInfo);
  void deleteUser(final UUID userId);
}
