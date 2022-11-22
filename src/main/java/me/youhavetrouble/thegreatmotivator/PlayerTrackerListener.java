package me.youhavetrouble.thegreatmotivator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerTrackerListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        TGMPlayer.trackPlayer(event.getPlayer().getUniqueId(), new TGMPlayer(0));
    }

}
