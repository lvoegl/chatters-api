package net.verotek.twitch.chatters.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ChannelNotFoundException extends Exception {

  public ChannelNotFoundException(String message) {
    super(message);
  }
}
