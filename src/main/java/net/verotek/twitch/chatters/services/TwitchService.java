package net.verotek.twitch.chatters.services;

import com.github.twitch4j.helix.domain.ChannelVip;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.Moderator;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.verotek.twitch.chatters.dtos.ChattersDto;
import net.verotek.twitch.chatters.dtos.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwitchService {

  private final HelixWrapperService helixWrapper;

  private final ModelMapper mapper;

  @Autowired
  public TwitchService(ModelMapper mapper, HelixWrapperService helixWrapper) {
    this.mapper = mapper;
    this.helixWrapper = helixWrapper;
  }

  public ChattersDto getCategorizedChatters(String channelId, String accessToken) {
    CompletableFuture<Set<UserDto>> chattersFuture =
        CompletableFuture.supplyAsync(() -> getChatters(channelId, accessToken));
    CompletableFuture<Set<UserDto>> moderatorsFuture =
        CompletableFuture.supplyAsync(() -> getModerators(channelId, accessToken));
    CompletableFuture<Set<UserDto>> vipsFuture =
        CompletableFuture.supplyAsync(() -> getVips(channelId, accessToken));

    return CompletableFuture.allOf(chattersFuture, moderatorsFuture, vipsFuture)
        .thenApply(
            res -> {
              Set<UserDto> chatters = chattersFuture.join();
              Set<UserDto> vips = vipsFuture.join();
              Set<UserDto> moderators = moderatorsFuture.join();

              ChattersDto chattersDTO = new ChattersDto();
              chatters.parallelStream()
                  .forEach(
                      chatter -> {
                        if (moderators.contains(chatter)) {
                          chattersDTO.addModerator(chatter);
                        } else if (vips.contains(chatter)) {
                          chattersDTO.addVip(chatter);
                        } else if (chatter.getId().equals(channelId)) {
                          chattersDTO.setBroadcaster(chatter);
                        } else {
                          chattersDTO.addViewer(chatter);
                        }
                      });

              return chattersDTO;
            })
        .join();
  }

  private Set<UserDto> mapToUsers(@NotNull Collection<?> collection) {
    return collection.parallelStream()
        .map(it -> mapper.map(it, UserDto.class))
        .collect(Collectors.toSet());
  }

  public Set<UserDto> getChatters(String channelId, String accessToken) {
    Set<Chatter> vips = helixWrapper.fetchChatters(channelId, accessToken);
    return mapToUsers(vips);
  }

  public Set<UserDto> getModerators(String channelId, String accessToken) {
    Set<Moderator> vips = helixWrapper.fetchModerators(channelId, accessToken);
    return mapToUsers(vips);
  }

  public Set<UserDto> getVips(String channelId, String accessToken) {
    Set<ChannelVip> vips = helixWrapper.fetchVips(channelId, accessToken);
    return mapToUsers(vips);
  }
}
