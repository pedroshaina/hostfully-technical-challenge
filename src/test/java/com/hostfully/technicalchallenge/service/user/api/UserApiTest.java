package com.hostfully.technicalchallenge.service.user.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.technicalchallenge.common.exception.NotFoundException;
import com.hostfully.technicalchallenge.service.user.api.request.UpsertUserRequest;
import com.hostfully.technicalchallenge.service.user.api.response.UserResponse;
import com.hostfully.technicalchallenge.service.user.domain.UserDto;
import com.hostfully.technicalchallenge.service.user.domain.UserMapper;
import com.hostfully.technicalchallenge.service.user.domain.UserMapperImpl;
import com.hostfully.technicalchallenge.service.user.domain.UserService;
import com.hostfully.technicalchallenge.util.RandomEntityGenerator;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {UserController.class})
@Import({UserMapperImpl.class})
public class UserApiTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserMapper userMapper;
  @MockBean
  private UserService userService;

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenGetUserById() {
    mockMvc
        .perform(get(UserApi.USER_API_PATH + "/{id}", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfUserDoesntExistWhenGetUserById() {
    final UUID userId = UUID.randomUUID();

    doThrow(new NotFoundException(String.format("No user found with id %s", userId)))
            .when(userService).retrieveUser(any(UUID.class));

    mockMvc
        .perform(get(UserApi.USER_API_PATH + "/{id}", userId))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithUserWhenGetUserById() {
    final UUID userId = UUID.randomUUID();

    final UserDto retrievedUser = RandomEntityGenerator.create(UserDto.class)
            .withId(userId);

    final UserResponse expectedResponse = userMapper.dtoToResponse(retrievedUser);

    doReturn(retrievedUser).when(userService).retrieveUser(any(UUID.class));

    mockMvc
        .perform(get(UserApi.USER_API_PATH + "/{id}", userId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidJsonWhenCreateUser() {
    mockMvc
        .perform(
            post(UserApi.USER_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfNameIsNullWhenCreateUser() {
    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withName(null)
        .withEmail("pedro.martins@example.com");

    mockMvc
        .perform(
            post(UserApi.USER_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfEmailIsNullWhenCreateUser() {
    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail(null);

    mockMvc
        .perform(
            post(UserApi.USER_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfEmailIsMalformedWhenCreateUser() {
    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail("invalid email");

    mockMvc
        .perform(
            post(UserApi.USER_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDateOfBirthIsNullWhenCreateUser() {
    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withDateOfBirth(null)
        .withEmail("pedro.martins@example.com");

    mockMvc
        .perform(
            post(UserApi.USER_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn201WithCreatedUserWhenCreateUser() {
    final UUID userId = UUID.randomUUID();

    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail("pedro.martins@example.com");

    doAnswer(invocation -> {

      final UserDto userInfo = invocation.getArgument(0, UserDto.class);
      return userInfo.withId(userId);

    }).when(userService).createUser(any(UserDto.class));

    final UserDto expectedResponse = userMapper.upsertRequestToDto(request)
            .withId(userId);

    mockMvc
        .perform(
            post(UserApi.USER_API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenUpdateUser() {
    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail("pedro.martins@example.com");

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidJsonIsProvidedWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfNameIsNullWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withName(null)
        .withEmail("pedro.martins@example.com");

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfEmailIsNullWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail(null);

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfEmailIsMalformedWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail("invalid-email");

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn422IfDateOfBirthIsNullWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withDateOfBirth(null)
        .withEmail("pedro.martins@example.com");

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  @SneakyThrows
  void shouldReturn404IfUserDoesntExistWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail("pedro.martins@example.com");

    doThrow(new NotFoundException(String.format("No user found with id %s", userId)))
        .when(userService).updateUser(any(UUID.class), any(UserDto.class));

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldReturn200WithUpdatedUserWhenUpdateUser() {
    final UUID userId = UUID.randomUUID();

    final UpsertUserRequest request = RandomEntityGenerator.create(UpsertUserRequest.class)
        .withEmail("pedro.martins@example.com");

    final UserDto persisted = userMapper.upsertRequestToDto(request)
            .withId(userId);

    final UserResponse expectedResponse = userMapper.dtoToResponse(persisted);

    doReturn(persisted).when(userService).updateUser(any(UUID.class), any(UserDto.class));

    mockMvc
        .perform(
            put(UserApi.USER_API_PATH + "/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
  }

  @Test
  @SneakyThrows
  void shouldReturn400IfInvalidUuidIsProvidedWhenDeleteUser() {
    mockMvc
        .perform(delete(UserApi.USER_API_PATH + "/{id}", "invalid-uuid"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturn204WhenDeleteUser() {
    final UUID userId = UUID.randomUUID();

    mockMvc
        .perform(delete(UserApi.USER_API_PATH + "/{id}", userId))
        .andDo(print())
        .andExpect(status().isNoContent());
  }
}
