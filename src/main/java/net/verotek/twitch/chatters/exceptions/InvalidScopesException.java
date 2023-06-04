package net.verotek.twitch.chatters.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidScopesException extends Exception {

  public InvalidScopesException(String message) {
    super(message);
  }
}
