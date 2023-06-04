package net.verotek.twitch.chatters.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter(AccessLevel.PRIVATE)
@Data
@ToString
@Schema(name = "Error")
public class ErrorDto {

  private Long timestamp;

  private Integer status;

  private String message;

  public ErrorDto() {
    LocalDateTime currentDate = LocalDateTime.now();
    Timestamp now = Timestamp.valueOf(currentDate);
    timestamp = now.getTime();
  }

  public ErrorDto(Throwable ex, HttpStatus status) {
    this();
    this.message = ex.getMessage();
    this.status = status.value();
  }
}
