package net.verotek.twitch.chatters.models;

import static com.google.common.truth.Truth.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;

class ChannelTests {

  @Test
  void testPastDateIsExpired() {
    Channel channel = new Channel();
    Date pastDate = new Date(new Date().getTime() / 2);
    channel.setExpiresAt(pastDate);

    assertThat(channel.isAccessTokenExpired()).isTrue();
  }

  @Test
  void testFutureDateIsNotExpired() {
    Channel channel = new Channel();
    Date pastDate = new Date(new Date().getTime() + 10 * 1000);
    channel.setExpiresAt(pastDate);

    assertThat(channel.isAccessTokenExpired()).isFalse();
  }
}
