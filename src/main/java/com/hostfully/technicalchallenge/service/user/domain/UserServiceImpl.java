package com.hostfully.technicalchallenge.service.user.domain;

import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import com.hostfully.technicalchallenge.service.user.data.User;
import com.hostfully.technicalchallenge.service.user.data.UserRepository;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto retrieveUser(final UUID userId) {
    Objects.requireNonNull(userId, "`userId` cannot be null");

    final User retrieved = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(String.format("No user found with id %s", userId)));

    return userMapper.entityToDto(retrieved);
  }

  @Override
  public UserDto createUser(final UserDto userInfo) {
    checkUserInfoIsNotNull(userInfo);

    final User toSave = userMapper.dtoToEntity(userInfo);
    final User saved = userRepository.save(toSave);
    return userMapper.entityToDto(saved);
  }

  @Override
  public UserDto updateUser(final UUID userId, final UserDto userInfo) {
    Objects.requireNonNull(userId, "`userId` cannot be null");
    checkUserInfoIsNotNull(userInfo);

    final User retrieved = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(String.format("No user found with id %s", userId)));

    retrieved.setName(userInfo.getName());
    retrieved.setEmail(userInfo.getEmail());
    retrieved.setDateOfBirth(userInfo.getDateOfBirth());

    final User saved = userRepository.save(retrieved);

    return userMapper.entityToDto(saved);
  }

  @Override
  public void deleteUser(final UUID userId) {
    Objects.requireNonNull(userId, "`userId` cannot be null");
    userRepository.deleteById(userId);
  }

  private void checkUserInfoIsNotNull(final UserDto userInfo) {
    Objects.requireNonNull(userInfo, "`userInfo` cannot be null");
    Objects.requireNonNull(userInfo.getName(), "`userInfo.getName()` cannot be null");
    Objects.requireNonNull(userInfo.getEmail(), "`userInfo.getEmail()` cannot be null");
    Objects.requireNonNull(userInfo.getDateOfBirth(), "`userInfo.getDateOfBirth()` cannot be null");
  }
}
