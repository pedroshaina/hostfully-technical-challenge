package com.hostfully.technicalchallenge.service.user.api;

import com.hostfully.technicalchallenge.service.user.api.request.UpsertUserRequest;
import com.hostfully.technicalchallenge.service.user.api.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "User API")
@RequestMapping(value = UserApi.USER_API_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public interface UserApi {
  String USER_API_PATH = "/users";

  @Operation(summary = "Retrieve an user by its ID")
  @ApiResponse(
      responseCode = "200",
      description = "The retrieved user",
      useReturnTypeSchema = true
  )
  @GetMapping(value = "/{id}")
  UserResponse retrieveUser(@PathVariable("id") final UUID userId);

  @Operation(summary = "Creates an user")
  @ApiResponse(
      responseCode = "201",
      description = "The created user",
      useReturnTypeSchema = true
  )
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  UserResponse createUser(@RequestBody @Validated final UpsertUserRequest userInfo);

  @Operation(summary = "Updates an user by its ID")
  @ApiResponse(
      responseCode = "200",
      description = "The updated user",
      useReturnTypeSchema = true
  )
  @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  UserResponse updateUser(@PathVariable("id") final UUID userId, @RequestBody @Validated final UpsertUserRequest userInfo);

  @Operation(summary = "Deletes an user by its ID")
  @ApiResponse(
      responseCode = "204",
      description = "The user was deleted"
  )
  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteUser(@PathVariable("id") final UUID userId);
}
