package net.verotek.twitch.chatters.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import net.verotek.twitch.chatters.dtos.ErrorDto;
import net.verotek.twitch.chatters.exceptions.ChannelNotFoundException;
import net.verotek.twitch.chatters.exceptions.InvalidTokenRequestException;
import net.verotek.twitch.chatters.services.TwitchIdentityService;
import net.verotek.twitch.chatters.services.TwitchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

  protected final TwitchService service;

  protected final TwitchIdentityService identityService;

  public BaseRestController(TwitchService service, TwitchIdentityService identityService) {
    this.identityService = identityService;
    this.service = service;
  }

  @ApiResponse(
      responseCode = "400",
      content = {
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorDto.class))
      })
  @ExceptionHandler({InvalidTokenRequestException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto handleBadRequests(Throwable ex) {
    return new ErrorDto(ex, HttpStatus.BAD_REQUEST);
  }

  @ApiResponse(
      responseCode = "404",
      content = {
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorDto.class))
      })
  @ExceptionHandler({ChannelNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDto handleNotFound(Throwable ex) {
    return new ErrorDto(ex, HttpStatus.NOT_FOUND);
  }

  @ApiResponse(
      responseCode = "500",
      content = {
        @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorDto.class))
      })
  @ExceptionHandler(Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDto handleServerError(Throwable ex) {
    LOGGER.error("Unhandled Exception: " + ex.getMessage());
    Throwable cause = new Exception("Unknown Exception");
    return new ErrorDto(cause, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
