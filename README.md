# Chatters API

TODO


## Docker

docker compose deployment example with postgres & nightly package image

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
