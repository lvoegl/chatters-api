package net.verotek.twitch.chatters.models;

import static com.google.common.truth.Truth.assertThat;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.domain.TwitchScopes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

  @Test
  void testChannelConvertsScopes() {
    List<TwitchScopes> scopes = new ArrayList<>();
    scopes.add(TwitchScopes.HELIX_AUTOMOD_SETTINGS_MANAGE);
    scopes.add(TwitchScopes.HELIX_CHANNEL_VIPS_MANAGE);

    List<String> scopeNames = scopes.stream().map(Enum::toString).toList();
    OAuth2Credential credential =
        new OAuth2Credential(null, "", null, null, null, null, scopeNames);
    Channel channel = new Channel(credential);

    assertThat(channel.getScopes()).isEqualTo(scopes);
  }
}
