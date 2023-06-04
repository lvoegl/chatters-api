package net.verotek.twitch.chatters.repositories;

import net.verotek.twitch.chatters.models.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {}
