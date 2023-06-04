package net.verotek.twitch.chatters.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import net.verotek.twitch.chatters.dtos.UserDto;
import net.verotek.twitch.chatters.exceptions.ChannelNotFoundException;
import net.verotek.twitch.chatters.exceptions.InvalidTokenRequestException;
import net.verotek.twitch.chatters.services.TwitchIdentityService;
import net.verotek.twitch.chatters.services.TwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${api.base_url}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Roles")
public class RolesController extends BaseRestController {

  @Autowired
  public RolesController(TwitchService service, TwitchIdentityService identityService) {
    super(service, identityService);
  }

  @GetMapping(value = "vips")
  Set<UserDto> vips(@RequestParam(name = "channelId") String channelId)
      throws InvalidTokenRequestException, ChannelNotFoundException {
    String accessToken = identityService.getAccessToken(channelId);
    return service.getVips(channelId, accessToken);
  }

  @GetMapping("moderators")
  Set<UserDto> moderators(@RequestParam(name = "channelId") String channelId)
      throws InvalidTokenRequestException, ChannelNotFoundException {
    String accessToken = identityService.getAccessToken(channelId);
    return service.getModerators(channelId, accessToken);
  }
}
