package net.verotek.twitch.chatters.controllers;

import net.verotek.twitch.chatters.exceptions.InvalidCodeException;
import net.verotek.twitch.chatters.exceptions.InvalidScopesException;
import net.verotek.twitch.chatters.exceptions.InvalidTokenRequestException;
import net.verotek.twitch.chatters.services.TwitchIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/")
public class AuthController {

  private final TwitchIdentityService identityService;

  @Autowired
  public AuthController(TwitchIdentityService identityService) {
    this.identityService = identityService;
  }

  @ExceptionHandler({
    InvalidTokenRequestException.class,
    InvalidCodeException.class,
    InvalidScopesException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleInvalidTokenRequest(Throwable ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    model.addAttribute("authUrl", identityService.getAuthorizationUrl());
    return "index";
  }

  @GetMapping("authorize")
  public String authorize(@RequestParam(name = "code") String code, ModelMap model)
      throws InvalidTokenRequestException, InvalidScopesException, InvalidCodeException {
    identityService.authorize(code);
    model.addAttribute("success", "Successfully added channel");
    return "index";
  }
}
