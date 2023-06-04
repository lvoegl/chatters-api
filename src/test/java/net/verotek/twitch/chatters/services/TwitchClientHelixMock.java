package net.verotek.twitch.chatters.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ChannelVip;
import com.github.twitch4j.helix.domain.ChannelVipList;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.ChattersList;
import com.github.twitch4j.helix.domain.HelixPagination;
import com.github.twitch4j.helix.domain.Moderator;
import com.github.twitch4j.helix.domain.ModeratorList;
import com.netflix.hystrix.HystrixCommand;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class TwitchClientHelixMock {

  private final TwitchClient client = mockHelix();

  private TwitchClient mockHelix() {
    TwitchClient twitchClient = mock(TwitchClient.class);
    when(twitchClient.getHelix()).thenReturn(mock(TwitchHelix.class));
    return twitchClient;
  }

  private ChannelVipList mockVipList(Set<ChannelVip> vips, String cursor) {
    HelixPagination pagination = mock(HelixPagination.class);
    when(pagination.getCursor()).thenReturn(cursor);

    ChannelVipList vipSet = mock(ChannelVipList.class);
    when(vipSet.getPagination()).thenReturn(pagination);
    when(vipSet.getData()).thenReturn(new ArrayList<>(vips));

    return vipSet;
  }

  private ModeratorList mockModeratorList(Set<Moderator> moderators, String cursor) {
    HelixPagination pagination = mock(HelixPagination.class);
    when(pagination.getCursor()).thenReturn(cursor);

    ModeratorList moderatorSet = mock(ModeratorList.class);
    when(moderatorSet.getPagination()).thenReturn(pagination);
    when(moderatorSet.getModerators()).thenReturn(new ArrayList<>(moderators));

    return moderatorSet;
  }

  private ChattersList mockChattersList(Set<Chatter> chatters, String cursor) {
    HelixPagination pagination = mock(HelixPagination.class);
    when(pagination.getCursor()).thenReturn(cursor);

    ChattersList chattersSet = mock(ChattersList.class);
    when(chattersSet.getPagination()).thenReturn(pagination);
    when(chattersSet.getChatters()).thenReturn(new ArrayList<>(chatters));

    return chattersSet;
  }

  private HystrixCommand<ChannelVipList> mockVipCommand(Set<ChannelVip> vips, String cursor) {
    ChannelVipList vipSet = mockVipList(vips, cursor);
    @SuppressWarnings("unchecked")
    HystrixCommand<ChannelVipList> vipCommand = mock(HystrixCommand.class);
    when(vipCommand.execute()).thenReturn(vipSet);

    return vipCommand;
  }

  private HystrixCommand<ModeratorList> mockModeratorCommand(
      Set<Moderator> moderators, String cursor) {
    ModeratorList moderatorSet = mockModeratorList(moderators, cursor);
    @SuppressWarnings("unchecked")
    HystrixCommand<ModeratorList> moderatorCommand = mock(HystrixCommand.class);
    when(moderatorCommand.execute()).thenReturn(moderatorSet);

    return moderatorCommand;
  }

  private HystrixCommand<ChattersList> mockChattersCommand(Set<Chatter> chatters, String cursor) {
    ChattersList chattersSet = mockChattersList(chatters, cursor);
    @SuppressWarnings("unchecked")
    HystrixCommand<ChattersList> chattersCommand = mock(HystrixCommand.class);
    when(chattersCommand.execute()).thenReturn(chattersSet);

    return chattersCommand;
  }

  public ChannelVip mockVip(String userId, String username) {
    ChannelVip vip = mock(ChannelVip.class);
    when(vip.getUserId()).thenReturn(userId);
    when(vip.getUserLogin()).thenReturn(username.toLowerCase());
    when(vip.getUserName()).thenReturn(username);

    return vip;
  }

  public Moderator mockModerator(String userId, String username) {
    Moderator moderator = mock(Moderator.class);
    when(moderator.getUserId()).thenReturn(userId);
    when(moderator.getUserLogin()).thenReturn(username.toLowerCase());
    when(moderator.getUserName()).thenReturn(username);

    return moderator;
  }

  public Chatter mockChatter(String userId, String username) {
    Chatter chatter = mock(Chatter.class);
    when(chatter.getUserId()).thenReturn(userId);
    when(chatter.getUserLogin()).thenReturn(username.toLowerCase());
    when(chatter.getUserName()).thenReturn(username);

    return chatter;
  }

  public void addVipPage(Set<ChannelVip> vips, String cursor, String nextCursor) {
    HystrixCommand<ChannelVipList> vipCommand = mockVipCommand(vips, nextCursor);
    TwitchHelix helix = client.getHelix();
    when(helix.getChannelVips(any(), any(), any(), any(), eq(cursor))).thenReturn(vipCommand);
  }

  public void addModeratorPage(Set<Moderator> moderators, String cursor, String nextCursor) {
    HystrixCommand<ModeratorList> moderatorCommand = mockModeratorCommand(moderators, nextCursor);
    TwitchHelix helix = client.getHelix();
    when(helix.getModerators(any(), any(), any(), eq(cursor), any())).thenReturn(moderatorCommand);
  }

  public void addChattersPage(Set<Chatter> chatters, String cursor, String nextCursor) {
    HystrixCommand<ChattersList> chattersCommand = mockChattersCommand(chatters, nextCursor);
    TwitchHelix helix = client.getHelix();
    when(helix.getChatters(any(), any(), any(), any(), eq(cursor))).thenReturn(chattersCommand);
  }

  public Set<Chatter> moderatorsToChatters(Set<Moderator> moderators) {
    return moderators.stream()
        .map(m -> mockChatter(m.getUserId(), m.getUserName()))
        .collect(Collectors.toSet());
  }

  public Set<Chatter> vipsToChatters(Set<ChannelVip> vips) {
    return vips.stream()
        .map(v -> mockChatter(v.getUserId(), v.getUserName()))
        .collect(Collectors.toSet());
  }

  public TwitchClient getClient() {
    return client;
  }
}
