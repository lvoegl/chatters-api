package net.verotek.twitch.chatters.services;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.ChannelVip;
import com.github.twitch4j.helix.domain.ChannelVipList;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.ChattersList;
import com.github.twitch4j.helix.domain.Moderator;
import com.github.twitch4j.helix.domain.ModeratorList;
import com.netflix.hystrix.HystrixCommand;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelixWrapperService {

  private final TwitchHelix helix;

  @Autowired
  public HelixWrapperService(TwitchClient twitchClient) {
    this.helix = twitchClient.getHelix();
  }

  public Set<Chatter> fetchChatters(String channelId, String accessToken) {
    Set<Chatter> allChatters = new HashSet<>();
    String cursor = null;

    do {
      HystrixCommand<ChattersList> chatterCommand =
          helix.getChatters(accessToken, channelId, channelId, null, cursor);
      ChattersList chattersList = chatterCommand.execute();
      List<Chatter> chatters = chattersList.getChatters();
      allChatters.addAll(chatters);

      cursor = chattersList.getPagination().getCursor();
    } while (cursor != null);

    return allChatters;
  }

  public Set<Moderator> fetchModerators(String channelId, String accessToken) {
    Set<Moderator> allModerators = new HashSet<>();
    String cursor = null;

    do {
      HystrixCommand<ModeratorList> moderatorCommand =
          helix.getModerators(accessToken, channelId, null, cursor, null);
      ModeratorList moderatorList = moderatorCommand.execute();
      List<Moderator> moderators = moderatorList.getModerators();
      allModerators.addAll(moderators);

      cursor = moderatorList.getPagination().getCursor();
    } while (cursor != null);

    return allModerators;
  }

  public Set<ChannelVip> fetchVips(String channelId, String accessToken) {
    Set<ChannelVip> allVips = new HashSet<>();
    String cursor = null;

    do {
      HystrixCommand<ChannelVipList> vipCommand =
          helix.getChannelVips(accessToken, channelId, null, null, cursor);
      ChannelVipList vipList = vipCommand.execute();
      List<ChannelVip> vips = vipList.getData();
      allVips.addAll(vips);

      cursor = vipList.getPagination().getCursor();
    } while (cursor != null);

    return allVips;
  }
}
