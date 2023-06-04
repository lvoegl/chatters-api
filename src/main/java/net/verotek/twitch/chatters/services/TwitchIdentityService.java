package net.verotek.twitch.chatters.services;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.domain.TwitchScopes;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.verotek.twitch.chatters.exceptions.ChannelNotFoundException;
import net.verotek.twitch.chatters.exceptions.InvalidCodeException;
import net.verotek.twitch.chatters.exceptions.InvalidScopesException;
import net.verotek.twitch.chatters.exceptions.InvalidTokenRequestException;
import net.verotek.twitch.chatters.models.Channel;
import net.verotek.twitch.chatters.repositories.ChannelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwitchIdentityService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TwitchIdentityService.class);

  public static final Set<TwitchScopes> REQUIRED_SCOPES =
      Set.of(
          TwitchScopes.HELIX_MODERATION_READ,
          TwitchScopes.HELIX_CHANNEL_VIPS_READ,
          TwitchScopes.HELIX_CHATTERS_READ);

  private final TwitchIdentityProvider identityProvider;
  private final ChannelRepository channelRepository;

  @Autowired
  public TwitchIdentityService(
      ChannelRepository channelRepository, TwitchIdentityProvider identityProvider) {
    this.channelRepository = channelRepository;
    this.identityProvider = identityProvider;
  }

  private static Set<String> getRequiredScopeNames() {
    return REQUIRED_SCOPES.stream().map(Enum::toString).collect(Collectors.toSet());
  }

  public String getAccessToken(String channelId)
      throws InvalidTokenRequestException, ChannelNotFoundException {
    Optional<Channel> optionalChannel = channelRepository.findById(channelId);
    if (optionalChannel.isEmpty()) {
      throw new ChannelNotFoundException("channel is not yet registered");
    }

    Channel channel = optionalChannel.get();
    if (channel.isAccessTokenExpired()) {
      refresh(channel);
    }

    return channel.getAccessToken();
  }

  public String getAuthorizationUrl() {
    List<Object> scopes = new ArrayList<>(REQUIRED_SCOPES);
    return identityProvider.getAuthenticationUrl(scopes, null);
  }

  public void authorize(String code)
      throws InvalidTokenRequestException, InvalidScopesException, InvalidCodeException {
    OAuth2Credential credential;
    try {
      credential = identityProvider.getCredentialByCode(code);
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage());
      throw new InvalidCodeException("provided code is not valid");
    }

    Optional<OAuth2Credential> optionalFullCredential =
        identityProvider.getAdditionalCredentialInformation(credential);
    if (optionalFullCredential.isEmpty()) {
      throw new InvalidTokenRequestException("could not request credential details");
    }

    OAuth2Credential fullCredential = optionalFullCredential.get();
    if (!isScopeListValid(fullCredential)) {
      throw new InvalidScopesException("missing scopes from autorization");
    }

    Channel channel = new Channel(fullCredential);
    channelRepository.save(channel);
  }

  private boolean isScopeListValid(OAuth2Credential credential) {
    Set<String> scopes = new HashSet<>(credential.getScopes());
    return getRequiredScopeNames().equals(scopes);
  }

  private void refresh(Channel channel) throws InvalidTokenRequestException {
    OAuth2Credential channelCredential =
        new OAuth2Credential(
            identityProvider.getProviderName(),
            channel.getAccessToken(),
            channel.getRefreshToken(),
            channel.getId(),
            null,
            null,
            channel.getScopeNames());

    Optional<OAuth2Credential> optionalCredential =
        identityProvider.refreshCredential(channelCredential);
    if (optionalCredential.isEmpty()) {
      throw new InvalidTokenRequestException("could not refresh credential, try reauthenticating");
    }

    OAuth2Credential credential = optionalCredential.get();
    channel.updateWithCredential(credential);
    channelRepository.save(channel);
  }
}
