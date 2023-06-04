package net.verotek.twitch.chatters;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChattersApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChattersApplication.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public TwitchClient twitchClient() {
    return TwitchClientBuilder.builder().withEnableHelix(true).build();
  }

  @Bean
  public TwitchIdentityProvider identityProvider(
      @Value("${twitch.client_secret}") String clientSecret,
      @Value("${twitch.client_id}") String clientId,
      @Value("${twitch.redirect_url}") String redirectUrl) {
    return new TwitchIdentityProvider(clientId, clientSecret, redirectUrl);
  }
}
