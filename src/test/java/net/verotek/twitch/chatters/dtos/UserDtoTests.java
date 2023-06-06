package net.verotek.twitch.chatters.dtos;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class UserDtoTests {

  @Test
  void testHashCodeById() {
    UserDto user1 = new UserDto("123", "");
    UserDto user2 = new UserDto("123", "test");

    assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
  }
}
