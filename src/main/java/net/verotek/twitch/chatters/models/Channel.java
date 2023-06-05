package net.verotek.twitch.chatters.models;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.domain.TwitchScopes;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Getter
@Setter
public class Channel {

  @Id private String id;

  @NotNull private String refreshToken;

  @NotNull private List<TwitchScopes> scopes;

  @NotNull private String accessToken;

  @NotNull private Date expiresAt;

  public Channel(OAuth2Credential credential) {
    id = credential.getUserId();
    refreshToken = credential.getRefreshToken();
    scopes = credential.getScopes().stream().map(Channel::getScopeByName).toList();
    accessToken = credential.getAccessToken();
    expiresAt = computeExpiresAt(credential.getExpiresIn());
  }

  private static TwitchScopes getScopeByName(String scopeName) {
    for (TwitchScopes scope : TwitchScopes.values()) {
      if (scope.toString().equals(scopeName)) {
        return scope;
      }
    }
    return null;
  }

  public void updateWithCredential(OAuth2Credential credential) {
    this.refreshToken = credential.getRefreshToken();
    this.accessToken = credential.getAccessToken();
    this.expiresAt = computeExpiresAt(credential.getExpiresIn());
  }

  private Date computeExpiresAt(Integer expiresIn) {
    Calendar expiresAt = Calendar.getInstance();
    if (expiresIn == null) return expiresAt.getTime();

    expiresAt.add(Calendar.SECOND, expiresIn);
    return expiresAt.getTime();
  }

  public List<String> getScopeNames() {
    return scopes.stream().map(Enum::name).collect(Collectors.toList());
  }

  public boolean isAccessTokenExpired() {
    Date now = new Date();
    return now.after(expiresAt);
  }
}
