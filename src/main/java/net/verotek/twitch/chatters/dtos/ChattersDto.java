package net.verotek.twitch.chatters.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@Data
@ToString
@Schema(name = "Chatters")
public class ChattersDto {

  private UserDto owner;

  private Set<UserDto> moderators;

  private Set<UserDto> vips;

  private Set<UserDto> viewers;

  public ChattersDto() {
    moderators = new HashSet<>();
    vips = new HashSet<>();
    viewers = new HashSet<>();
    owner = null;
  }

  public void setOwner(UserDto owner) {
    this.owner = owner;
  }

  public void addModerator(UserDto moderator) {
    moderators.add(moderator);
  }

  public void addVip(UserDto vip) {
    vips.add(vip);
  }

  public void addViewer(UserDto viewer) {
    viewers.add(viewer);
  }
}
