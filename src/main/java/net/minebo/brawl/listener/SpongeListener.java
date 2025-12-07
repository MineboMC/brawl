package net.minebo.brawl.listener;

import net.minebo.brawl.Brawl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpongeListener implements Listener {

    private final Set<UUID> launched = new HashSet<>();

    private final double upwardVelocity = 3;
    private final long noFallTicks = 60L;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        Material below = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
        Material before = event.getFrom().getBlock().getRelative(BlockFace.DOWN).getType();

        // Player just stepped onto a sponge
        if ((below == Material.SPONGE || below == Material.WET_SPONGE) && before != below) {

            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);
            // Launch upward (keeps horizontal momentum)
            Vector current = player.getVelocity();
            player.setVelocity(new Vector(current.getX(), upwardVelocity, current.getZ()));

            UUID uuid = player.getUniqueId();
            launched.add(uuid);

            // Remove no-fall after 3 seconds
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> launched.remove(uuid), noFallTicks);
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        Player player = (Player) event.getEntity();

        if (launched.contains(player.getUniqueId())) {
            event.setCancelled(true);
            launched.remove(player.getUniqueId());
        }
    }

}
