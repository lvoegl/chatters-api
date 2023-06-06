package net.verotek.twitch.chatters.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@ToString
@Schema(name = "User")
public class UserDto {

  private String id;

  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDto userDTO = (UserDto) o;
    return Objects.equals(id, userDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
