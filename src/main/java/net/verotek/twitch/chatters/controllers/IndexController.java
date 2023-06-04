package net.verotek.twitch.chatters.controllers;

import net.verotek.twitch.chatters.services.TwitchIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class IndexController {

  private final TwitchIdentityService identityService;

  @Autowired
  public IndexController(TwitchIdentityService identityService) {
    this.identityService = identityService;
  }

  @GetMapping("error")
  public String error() {
    return "error";
  }

  @GetMapping("/")
  public String home(Model model) {
    String authUrl = identityService.getAuthorizationUrl();
    model.addAttribute("authUrl", authUrl);
    return "index";
  }
}
