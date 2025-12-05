package net.minebo.brawl.listener;

import com.mongodb.client.model.Filters;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.SpawnHotbar;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class BrawlListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BrawlProfile profile = BrawlProfile.get(event.getPlayer());

        // If not already in memory, attempt DB lookup (should rarely happen if pre-load works)
        if (profile == null) {
            Document doc = net.minebo.brawl.Brawl.getInstance()
                    .getMongoHandler()
                    .profileCollection
                    .find(Filters.eq("uniqueId", event.getPlayer().getUniqueId().toString()))
                    .first();

            if (doc != null) {
                profile = new BrawlProfile(doc);
            } else {
                profile = new BrawlProfile(event.getPlayer());
            }
        }

        profile.setLastUsername(event.getPlayer().getName());

        Kit.clear(event.getPlayer());
        event.getPlayer().teleport(Bukkit.getWorlds().getFirst().getSpawnLocation());
        SpawnHotbar.giveItems(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.getPlayer().setRespawnLocation(Bukkit.getWorlds().getFirst().getSpawnLocation());

        BrawlProfile.get(event.getPlayer()).spawnProtected = true;

        Kit.clear(event.getPlayer());
        SpawnHotbar.giveItems(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        BrawlProfile profile = BrawlProfile.get(event.getPlayer());

        profile.save();
    }

}
