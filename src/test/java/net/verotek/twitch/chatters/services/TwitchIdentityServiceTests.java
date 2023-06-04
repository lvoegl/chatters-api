package net.verotek.twitch.chatters.services;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.verotek.twitch.chatters.exceptions.ChannelNotFoundException;
import net.verotek.twitch.chatters.exceptions.InvalidCodeException;
import net.verotek.twitch.chatters.exceptions.InvalidScopesException;
import net.verotek.twitch.chatters.exceptions.InvalidTokenRequestException;
import net.verotek.twitch.chatters.models.Channel;
import net.verotek.twitch.chatters.repositories.ChannelRepository;
import org.junit.jupiter.api.Test;

class TwitchIdentityServiceTests {

  @Test
  void testAuthorizeCodeRequestFails() {
    ChannelRepository repository = mock(ChannelRepository.class);
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);
    when(identityProvider.getCredentialByCode(any())).thenThrow(new RuntimeException());

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);

    assertThrows(InvalidCodeException.class, () -> identityService.authorize(null));
  }

  @Test
  void testAuthorizeAdditionalRequestFails() {
    ChannelRepository repository = mock(ChannelRepository.class);
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);
    when(identityProvider.getAdditionalCredentialInformation(any())).thenReturn(Optional.empty());

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);

    assertThrows(InvalidTokenRequestException.class, () -> identityService.authorize(null));
  }

  @Test
  void testAuthorizeEmptyScopeToken() {
    ChannelRepository repository = mock(ChannelRepository.class);
    OAuth2Credential credential = mock(OAuth2Credential.class);
    when(credential.getScopes()).thenReturn(new ArrayList<>());
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);
    when(identityProvider.getAdditionalCredentialInformation(any()))
        .thenReturn(Optional.of(credential));

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);

    assertThrows(InvalidScopesException.class, () -> identityService.authorize(null));
  }

  @Test
  void testAuthorizeSavesValidToken() throws Exception {
    ChannelRepository repository = mock(ChannelRepository.class);
    OAuth2Credential credential = mock(OAuth2Credential.class);
    List<String> requiredScopes =
        TwitchIdentityService.REQUIRED_SCOPES.stream().map(Enum::toString).toList();
    when(credential.getScopes()).thenReturn(requiredScopes);
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);
    when(identityProvider.getAdditionalCredentialInformation(any()))
        .thenReturn(Optional.of(credential));

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);
    identityService.authorize(null);

    verify(repository, times(1)).save(any());
  }

  @Test
  void testGetAccessTokenUnregisteredChannel() {
    ChannelRepository repository = mock(ChannelRepository.class);
    when(repository.findById(any())).thenReturn(Optional.empty());
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);

    assertThrows(ChannelNotFoundException.class, () -> identityService.getAccessToken(null));
  }

  @Test
  void testGetAccessTokenReturnsValidChannelToken() throws Exception {
    ChannelRepository repository = mock(ChannelRepository.class);
    String token = "token";
    Date futureDate = new Date(new Date().getTime() * 2);
    Channel validChannel = new Channel("", "", new ArrayList<>(), token, futureDate);
    when(repository.findById(any())).thenReturn(Optional.of(validChannel));
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);

    assertThat(identityService.getAccessToken(null)).isEqualTo(token);
  }

  @Test
  void testGetAccessTokenRefreshFails() {
    ChannelRepository repository = mock(ChannelRepository.class);
    Date pastDate = new Date(new Date().getTime() / 2);
    Channel validChannel = new Channel("", "", new ArrayList<>(), "", pastDate);
    when(repository.findById(any())).thenReturn(Optional.of(validChannel));
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);
    when(identityProvider.refreshCredential(any())).thenReturn(Optional.empty());

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);

    assertThrows(InvalidTokenRequestException.class, () -> identityService.getAccessToken(null));
  }

  @Test
  void testGetAccessTokenRefreshSaves() throws Exception {
    ChannelRepository repository = mock(ChannelRepository.class);
    Date pastDate = new Date(new Date().getTime() / 2);
    Channel expiredChannel = new Channel("", "", new ArrayList<>(), "", pastDate);
    when(repository.findById(any())).thenReturn(Optional.of(expiredChannel));
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);
    String token = "token";
    OAuth2Credential credential = new OAuth2Credential(null, token);
    when(identityProvider.refreshCredential(any())).thenReturn(Optional.of(credential));

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);
    identityService.getAccessToken(null);

    verify(repository, times(1)).save(any());
  }

  @Test
  void testGetAccessTokenRefresh() throws Exception {
    ChannelRepository repository = mock(ChannelRepository.class);
    Date pastDate = new Date(new Date().getTime() / 2);
    Channel validChannel = new Channel("", "", new ArrayList<>(), "", pastDate);
    when(repository.findById(any())).thenReturn(Optional.of(validChannel));
    TwitchIdentityProvider identityProvider = mock(TwitchIdentityProvider.class);
    String token = "token";
    OAuth2Credential credential = new OAuth2Credential(null, token);
    when(identityProvider.refreshCredential(any())).thenReturn(Optional.of(credential));

    TwitchIdentityService identityService = new TwitchIdentityService(repository, identityProvider);

    assertThat(identityService.getAccessToken(null)).isEqualTo(token);
  }
}
