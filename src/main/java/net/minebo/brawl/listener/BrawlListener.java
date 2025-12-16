package net.minebo.brawl.listener;

import com.mongodb.client.model.Filters;
import net.minebo.basalt.api.BasaltAPI;
import net.minebo.basalt.models.profile.GameProfile;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.SpawnHotbar;
import net.minebo.cobalt.util.ColorUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.concurrent.ExecutionException;

public class BrawlListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        BrawlProfile profile = BrawlProfile.get(event.getPlayer());

        event.setJoinMessage(null);

        if(!event.getPlayer().hasPlayedBefore()) {
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ColorUtil.translateColors(event.getPlayer().getDisplayName() + " &ehas joined for the first time! &7(&f#" + BrawlProfile.profiles.size() + 1 + "&7)")));
        } else if(Bukkit.getOnlinePlayers().size() <= 20) {
            try {
                GameProfile basaltProfile = BasaltAPI.INSTANCE.quickFindProfile(event.getPlayer().getUniqueId()).get();

                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ColorUtil.translateColors(basaltProfile.getCurrentRank().getColor() + event.getPlayer().getName() + " &6joined.")));
            } catch (Exception e) {
                Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ColorUtil.translateColors(event.getPlayer().getDisplayName() + " &6joined.")));
            }
        }

        if(Kit.freeKitMode) {
            event.getPlayer().sendMessage(ColorUtil.translateColors("&eAll kits are currently free, try them out!"));
        }

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
        event.setRespawnLocation(Bukkit.getWorlds().getFirst().getSpawnLocation());

        BrawlProfile.get(event.getPlayer()).spawnProtected = true;

        Kit.clear(event.getPlayer());
        SpawnHotbar.giveItems(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        BrawlProfile profile = BrawlProfile.get(event.getPlayer());

        event.setQuitMessage(null);

        if(Bukkit.getOnlinePlayers().size() <= 20) {
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(ColorUtil.translateColors(event.getPlayer().getDisplayName() + " &6left.")));
        }

        profile.save();
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            event.setFoodLevel(20);
            player.setSaturation(20.0f);
        }
    }

}
