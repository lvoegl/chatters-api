package net.verotek.twitch.chatters.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidCodeException extends Exception {

  public InvalidCodeException(String message) {
    super(message);
  }
}
