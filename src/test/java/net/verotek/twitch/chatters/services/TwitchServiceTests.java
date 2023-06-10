package net.verotek.twitch.chatters.services;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.twitch4j.helix.domain.ChannelVip;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.Moderator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.verotek.twitch.chatters.dtos.ChattersDto;
import net.verotek.twitch.chatters.dtos.UserDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class TwitchServiceTests {

  private static final ModelMapper MAPPER = new ModelMapper();

  private HelixWrapperService mockHelixWrapper(
      Set<Moderator> moderators, Set<ChannelVip> vips, Set<Chatter> chatters) {
    HelixWrapperService helixWrapper = mock(HelixWrapperService.class);
    when(helixWrapper.fetchModerators(any(), any())).thenReturn(moderators);
    when(helixWrapper.fetchVips(any(), any())).thenReturn(vips);
    when(helixWrapper.fetchChatters(any(), any())).thenReturn(chatters);

    return helixWrapper;
  }

  private UserDto mapToUser(Object obj) {
    return MAPPER.map(obj, UserDto.class);
  }

  private Set<UserDto> mapToUsers(Collection<?> collection) {
    return collection.stream().map(this::mapToUser).collect(Collectors.toSet());
  }

  @Test
  void testGetModerators() {
    TwitchClientHelixMock helixMock = new TwitchClientHelixMock();

    Set<Moderator> moderators = new HashSet<>();
    moderators.add(helixMock.mockModerator("56", "mod1"));
    moderators.add(helixMock.mockModerator("95", "mod2"));
    moderators.add(helixMock.mockModerator("29", "mod3"));

    Set<UserDto> expectedUsers = mapToUsers(moderators);

    HelixWrapperService helixWrapperMock = mockHelixWrapper(moderators, null, null);
    TwitchService twitchService = new TwitchService(MAPPER, helixWrapperMock);
    Set<UserDto> users = twitchService.getModerators(null, null);

    assertThat(users).isEqualTo(expectedUsers);
  }

  @Test
  void testGetVips() {
    TwitchClientHelixMock helixMock = new TwitchClientHelixMock();

    Set<ChannelVip> vips = new HashSet<>();
    vips.add(helixMock.mockVip("19", "vip1"));
    vips.add(helixMock.mockVip("64", "vip2"));
    vips.add(helixMock.mockVip("94", "vip3"));

    Set<UserDto> expectedUsers = mapToUsers(vips);

    HelixWrapperService helixWrapperMock = mockHelixWrapper(null, vips, null);
    TwitchService twitchService = new TwitchService(MAPPER, helixWrapperMock);
    Set<UserDto> users = twitchService.getVips(null, null);

    assertThat(users).isEqualTo(expectedUsers);
  }

  @Test
  void testGetChatters() {
    TwitchClientHelixMock helixMock = new TwitchClientHelixMock();

    Set<Chatter> chatters = new HashSet<>();
    chatters.add(helixMock.mockChatter("65", "chatter1"));
    chatters.add(helixMock.mockChatter("96", "chatter2"));
    chatters.add(helixMock.mockChatter("74", "chatter3"));

    Set<UserDto> expectedUsers = mapToUsers(chatters);

    HelixWrapperService helixWrapperMock = mockHelixWrapper(null, null, chatters);
    TwitchService twitchService = new TwitchService(MAPPER, helixWrapperMock);
    Set<UserDto> users = twitchService.getChatters(null, null);

    assertThat(users).isEqualTo(expectedUsers);
  }

  @Test
  void testGetCategorizedChatters() {
    TwitchClientHelixMock helixMock = new TwitchClientHelixMock();

    Chatter broadcaster = helixMock.mockChatter("14", "broadcaster1");

    Set<Moderator> moderators = new HashSet<>();
    moderators.add(helixMock.mockModerator("17", "mod1"));
    moderators.add(helixMock.mockModerator("34", "mod2"));
    moderators.add(helixMock.mockModerator("95", "mod3"));

    Set<ChannelVip> vips = new HashSet<>();
    vips.add(helixMock.mockVip("28", "vip5"));
    vips.add(helixMock.mockVip("16", "vip6"));
    vips.add(helixMock.mockVip("84", "vip8"));

    Set<Chatter> viewers = new HashSet<>();
    viewers.add(helixMock.mockChatter("53", "chatter1"));
    viewers.add(helixMock.mockChatter("80", "chatter2"));
    viewers.add(helixMock.mockChatter("67", "chatter3"));

    Set<Chatter> allChatters = new HashSet<>();
    allChatters.add(broadcaster);
    allChatters.addAll(helixMock.moderatorsToChatters(moderators));
    allChatters.addAll(helixMock.vipsToChatters(vips));
    allChatters.addAll(viewers);

    ChattersDto expectedChatters =
        new ChattersDto(
            mapToUser(broadcaster), mapToUsers(moderators), mapToUsers(vips), mapToUsers(viewers));

    HelixWrapperService helixWrapperMock = mockHelixWrapper(moderators, vips, allChatters);
    TwitchService twitchService = new TwitchService(MAPPER, helixWrapperMock);
    ChattersDto chatters = twitchService.getCategorizedChatters(broadcaster.getUserId(), null);

    assertThat(chatters).isEqualTo(expectedChatters);
  }
}
