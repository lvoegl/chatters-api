package net.verotek.twitch.chatters.services;

import static com.google.common.truth.Truth.assertThat;

import com.github.twitch4j.helix.domain.ChannelVip;
import com.github.twitch4j.helix.domain.Chatter;
import com.github.twitch4j.helix.domain.Moderator;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HelixWrapperTests {

  @Test
  void testFetchChatters() {
    TwitchClientHelixMock helixMock = new TwitchClientHelixMock();

    Set<Chatter> chatters1 = new HashSet<>();
    chatters1.add(helixMock.mockChatter("93", "chatter1"));
    chatters1.add(helixMock.mockChatter("21", "chatter2"));

    Set<Chatter> chatters2 = new HashSet<>();
    chatters2.add(helixMock.mockChatter("53", "chatter3"));
    chatters2.add(helixMock.mockChatter("11", "chatter4"));

    Set<Chatter> allChatters = new HashSet<>();
    allChatters.addAll(chatters1);
    allChatters.addAll(chatters2);

    helixMock.addChattersPage(chatters1, null, "cursor1");
    helixMock.addChattersPage(chatters2, "cursor1", null);

    HelixWrapperService helixWrapper = new HelixWrapperService(helixMock.getClient());
    Set<Chatter> chatters = helixWrapper.fetchChatters(null, null);

    assertThat(chatters).isEqualTo(allChatters);
  }

  @Test
  void testFetchModerators() {
    TwitchClientHelixMock helixMock = new TwitchClientHelixMock();

    Set<Moderator> moderators1 = new HashSet<>();
    moderators1.add(helixMock.mockModerator("32", "mod1"));
    moderators1.add(helixMock.mockModerator("28", "mod2"));

    Set<Moderator> moderators2 = new HashSet<>();
    moderators2.add(helixMock.mockModerator("64", "mod3"));
    moderators2.add(helixMock.mockModerator("17", "mod4"));

    Set<Moderator> allModerators = new HashSet<>();
    allModerators.addAll(moderators1);
    allModerators.addAll(moderators2);

    helixMock.addModeratorPage(moderators1, null, "cursor1");
    helixMock.addModeratorPage(moderators2, "cursor1", null);

    HelixWrapperService helixWrapper = new HelixWrapperService(helixMock.getClient());
    Set<Moderator> moderators = helixWrapper.fetchModerators(null, null);

    assertThat(moderators).isEqualTo(allModerators);
  }

  @Test
  void testFetchVips() {
    TwitchClientHelixMock helixMock = new TwitchClientHelixMock();

    Set<ChannelVip> vips1 = new HashSet<>();
    vips1.add(helixMock.mockVip("1", "user1"));
    vips1.add(helixMock.mockVip("2", "user2"));

    Set<ChannelVip> vips2 = new HashSet<>();
    vips2.add(helixMock.mockVip("3", "user3"));
    vips2.add(helixMock.mockVip("4", "user17"));

    Set<ChannelVip> allVips = new HashSet<>();
    allVips.addAll(vips1);
    allVips.addAll(vips2);

    helixMock.addVipPage(vips1, null, "cursor1");
    helixMock.addVipPage(vips2, "cursor1", null);

    HelixWrapperService helixWrapper = new HelixWrapperService(helixMock.getClient());
    Set<ChannelVip> vips = helixWrapper.fetchVips(null, null);

    assertThat(vips).isEqualTo(allVips);
  }
}
