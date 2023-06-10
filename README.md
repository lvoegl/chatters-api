# Chatters API

Twitch has restricted access to channels' chatters, moderator and VIP lists.
These features are now only available via Twitch's Helix API.
Chatters API provides a channel's VIP/moderator list and recreates the chatters list if the broadcaster has authorized the Chatters API Twitch application.

## Prerequisites
- Java 17
- optionally Docker for building containerized application

## Developing
Set environment variables

`CHATTERS_DEV_CLIENT_ID`

`CHATTERS_DEV_CLIENT_SECRET`

to your application's client id and secret with callback url `http://localhost:8080`

**Run the application with dev profile:**
```
./gradlew bootRunDev
```

## Testing
**Execute unit tests:**
```
./gradlew test
```

## Formatting
Java source code should comply with [Google's Java Style](https://google.github.io/styleguide/javaguide.html).
Formatting for this project is enforced by [Spotless](https://github.com/diffplug/spotless).

**Check formatting:**
```
./gradlew spotlessCheck
```

**Apply formatting:**
```
./gradlew spotlessApply
```

## Docker Image
The `nightly` tagged image is automatically built and published to the [GitHub packages section](https://github.com/users/lvoegl/packages/container/package/chatters-api) at every push to the main branch

To build an image yourself run `./gradlew bootBuildImage`

### Deploying with Docker
Below you can find a docker compose deployment example with postgres & nightly package image

```
version: "3.9"

services:
  db:
    image: postgres:15
    restart: unless-stopped
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: chatters
    networks:
      - db_net

  chatters:
    image: ghcr.io/lvoegl/chatters-api:nightly
    command:
      - "--spring.profiles.active=prod"
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
#      CHATTERS_TWITCH_CLIENT_SECRET: CLIENT_SECRET
#      CHATTERS_TWITCH_CLIENT_ID: CLIENT_ID
#      CHATTERS_TWITCH_REDIRECT_URL: http://localhost:8080/authorize
      CHATTERS_DB_URL: jdbc:postgresql://db:5432/chatters
      CHATTERS_DB_USER: postgres
      CHATTERS_DB_PASSWORD: postgres
    networks:
      - chatters_net
      - db_net

networks:
  chatters_net:
  db_net:
    internal: true
```
