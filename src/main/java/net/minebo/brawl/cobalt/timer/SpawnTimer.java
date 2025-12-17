package net.minebo.brawl.cobalt.timer;

import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.kit.impl.Phantom;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.brawl.spawn.SpawnHotbar;
import net.minebo.cobalt.timer.Timer;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnTimer extends Timer {
    public static final Map<UUID, Task> spawnTasks = new HashMap<>();

    public SpawnTimer(Player player, Plugin plugin) {
        super(player, 10, spawnTasks, plugin);
    }

    @Override
    protected void onStart() {
        player.sendMessage(ColorUtil.translateColors("&eTeleporting you to spawn in 10 seconds."));
    }

    @Override
    protected boolean onTick(int secondsLeft) {
        if (player.hasMetadata("frozen")) {
            player.sendMessage(ColorUtil.translateColors("&cYour teleport to spawn has cancelled since you are frozen."));
            return false;
        }
        return true;
    }

    @Override
    protected void onComplete() {
        BrawlProfile profile = BrawlProfile.get(player);

        if (profile.getSelectedKit() instanceof Phantom) {
            Phantom.FlightTimer.flightTasks.remove(player.getUniqueId());
        }

        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        player.sendMessage(ColorUtil.translateColors("&eYou have been teleported to spawn!"));

        if (!profile.isSpawnProtected()) {
            profile.spawnProtected = true;
            player.sendMessage(ColorUtil.translateColors("&aYour spawn protection has been enabled."));
        }

        Kit.clear(player);
        SpawnHotbar.giveItems(player);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(event.hasChangedOrientation()) {
            return;
        }

        if(spawnTasks.containsKey(player.getUniqueId())) {
            player.sendMessage(ColorUtil.translateColors("&cYour teleport to spawn has cancelled since you moved."));
            spawnTasks.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onTakeDamage(EntityDamageByEntityEvent event) {
        if(spawnTasks.containsKey(event.getEntity().getUniqueId())) {
            player.sendMessage(ColorUtil.translateColors("&cYour teleport to spawn has cancelled since you were hit."));
            spawnTasks.remove(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    public void onDoDamage(EntityDamageByEntityEvent event) {
        if(spawnTasks.containsKey(event.getDamager().getUniqueId())) {
            player.sendMessage(ColorUtil.translateColors("&cYour teleport to spawn has cancelled since you hit a player."));
            spawnTasks.remove(event.getDamager().getUniqueId());
        }
    }

}