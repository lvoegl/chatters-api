package net.verotek.twitch.chatters.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.verotek.twitch.chatters.dtos.ChattersDto;
import net.verotek.twitch.chatters.services.TwitchIdentityService;
import net.verotek.twitch.chatters.services.TwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${api.base_url}/chatters", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Chatters")
public class ChattersController extends BaseRestController {

  @Autowired
  public ChattersController(TwitchService service, TwitchIdentityService identityService) {
    super(service, identityService);
  }

  @GetMapping("all")
  public ChattersDto all(@RequestParam(name = "channelId") String channelId) throws Throwable {
    String accessToken = identityService.getAccessToken(channelId);
    return service.getCategorizedChatters(channelId, accessToken);
  }
}
