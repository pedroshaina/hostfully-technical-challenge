package com.hostfully.technicalchallenge.service.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import com.hostfully.technicalchallenge.service.user.data.User;
import com.hostfully.technicalchallenge.service.user.data.UserRepository;
import com.hostfully.technicalchallenge.util.RandomEntityGenerator;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Spy
  private UserMapper userMapper = new UserMapperImpl();

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void shouldThrowNullPointerExceptionIfUserIdIsNullWhenRetrieveUser() {
    assertThatThrownBy(() -> userService.retrieveUser(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfUserDoesntExistWhenRetrieveUser() {
    doReturn(Optional.empty()).when(userRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> userService.retrieveUser(UUID.randomUUID()))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldRetrieveUser() {
    final UUID userId = UUID.randomUUID();

    final User user = RandomEntityGenerator.create(User.class)
            .withId(userId);

    doReturn(Optional.of(user)).when(userRepository).findById(any(UUID.class));

    final UserDto retrievedUser = userService.retrieveUser(userId);

    final UserDto expected = userMapper.entityToDto(user);

    assertThat(retrievedUser).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfUserInfoIsNullWhenCreateUser() {
    assertThatThrownBy(() -> userService.createUser(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfNameIsNullWhenCreateUser() {
    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
            .withName(null);

    assertThatThrownBy(() -> userService.createUser(userInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfEmailIsNullWhenCreateUser() {
    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withEmail(null);

    assertThatThrownBy(() -> userService.createUser(userInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfDateOfBirthIsNullWhenCreateUser() {
    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withDateOfBirth(null);

    assertThatThrownBy(() -> userService.createUser(userInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldCreateUser() {
    final UUID userId = UUID.randomUUID();
    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withId(null);

    doAnswer(invocation -> {

      final User userToBeSaved = invocation.getArgument(0, User.class);
      return userToBeSaved.withId(userId);

    }).when(userRepository).save(any(User.class));

    final UserDto saved = userService.createUser(userInfo);
    final UserDto expected = userInfo.withId(userId);

    assertThat(saved).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfUserIdIsNullWhenUpdateUser() {
    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withId(null);

    assertThatThrownBy(() -> userService.updateUser(null, userInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfUserInfoIsNullWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    assertThatThrownBy(() -> userService.updateUser(userId, null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfNameIsNullWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withId(null)
        .withName(null);

    assertThatThrownBy(() -> userService.updateUser(userId, userInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfEmailIsNullWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withId(null)
        .withEmail(null);

    assertThatThrownBy(() -> userService.updateUser(userId, userInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNullPointerExceptionIfDateOfBirthIsNullWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withId(null)
        .withDateOfBirth(null);

    assertThatThrownBy(() -> userService.updateUser(userId, userInfo))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldThrowNotFoundExceptionIfUserDoesntExistWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withId(null);

    doReturn(Optional.empty()).when(userRepository).findById(any(UUID.class));

    assertThatThrownBy(() -> userService.updateUser(userId, userInfo))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void shouldUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UserDto userInfo = RandomEntityGenerator.create(UserDto.class)
        .withId(null);

    final User retrievedUser = RandomEntityGenerator.create(User.class)
        .withId(userId);

    doAnswer(AdditionalAnswers.returnsFirstArg()).when(userRepository).save(any(User.class));
    doReturn(Optional.of(retrievedUser)).when(userRepository).findById(any(UUID.class));

    final UserDto updated = userService.updateUser(userId, userInfo);
    final UserDto expected = userInfo.withId(userId);

    assertThat(updated).isEqualTo(expected);
  }

  @Test
  void shouldThrowNullPointerExceptionIfUserIdIsNullWhenDeleteUser() {
    assertThatThrownBy(() -> userService.deleteUser(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldDeleteUser() {
    final UUID userId = UUID.randomUUID();

    userService.deleteUser(userId);

    verify(userRepository).deleteById(any(UUID.class));
  }
}
