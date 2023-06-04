package net.verotek.twitch.chatters.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidTokenRequestException extends Exception {

  public InvalidTokenRequestException(String message) {
    super(message);
  }
}
