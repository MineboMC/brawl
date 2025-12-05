package net.minebo.brawl.listener;

import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.kregions.manager.FlagManager;
import net.minebo.kregions.manager.RegionManager;
import net.minebo.kregions.model.Region;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.projectiles.ProjectileSource;
import java.util.concurrent.TimeUnit;

public class ProtectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!Bukkit.getPluginManager().isPluginEnabled("kRegions")) return;
        Region region = RegionManager.getRegionByLocation(player.getLocation());
        if (region != null && region.containsFlag(FlagManager.getFlagByName("SafeZone"))) {
            BrawlProfile profile = BrawlProfile.get(player);
            if (profile != null) {
                profile.spawnProtected = true;
                player.sendMessage(ColorUtil.translateColors("&aYou are protected by spawn!"));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!Bukkit.getPluginManager().isPluginEnabled("kRegions")) return;

        BrawlProfile profile = BrawlProfile.get(player);
        if (profile == null) return;

        Location toLoc = event.getTo();
        Location fromLoc = event.getFrom();
        if (toLoc == null || fromLoc == null) return;

        Region toRegion = RegionManager.getRegionByLocation(toLoc);
        Region fromRegion = RegionManager.getRegionByLocation(fromLoc);

        boolean fromSafe = (fromRegion != null && fromRegion.containsFlag(FlagManager.getFlagByName("SafeZone")));
        boolean toSafe = (toRegion != null && toRegion.containsFlag(FlagManager.getFlagByName("SafeZone")));

        // Leaving SafeZone: break spawn protection
        if (profile.isSpawnProtected() && !toSafe && fromSafe) {
            profile.spawnProtected = false;
            player.sendMessage(ColorUtil.translateColors("&cYour spawn protection has been broken!"));

            if(profile.getSelectedKit() == null) {
                if(profile.lastKit == null) {
                    Kit.get("PvP").apply(player);
                } else {
                    Kit.get(profile.lastKit).apply(player);
                }
            }
        }

        // Entering SafeZone by walking: bounce back unless already inside
        if (!fromSafe && toSafe) {
            event.setTo(fromLoc); // Teleport back to previous position
            player.sendMessage(ColorUtil.translateColors("&cYou cannot enter the spawn region!"));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player target = (Player) event.getEntity();
        BrawlProfile targetProfile = BrawlProfile.get(target);
        if (targetProfile == null) return;

        Player attacker = null;
        // Direct attack
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        }
        // Arrow attack
        else if (event.getDamager() instanceof Arrow) {
            ProjectileSource shooter = ((Arrow) event.getDamager()).getShooter();
            if (shooter instanceof Player) attacker = (Player) shooter;
        }
        if (attacker == null) return;

        BrawlProfile attackerProfile = BrawlProfile.get(attacker);
        if (attackerProfile == null) return;

        // If target is protected, cancel damage and send message
        if (targetProfile.isSpawnProtected()) {
            event.setCancelled(true);
            attacker.sendMessage(ColorUtil.translateColors("&cThis player currently has spawn protection!"));
            return;
        }
    }

    // Prevent all types of damage for spawn protected players
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        BrawlProfile profile = BrawlProfile.get(player);
        if (profile != null && profile.isSpawnProtected()) {
            event.setCancelled(true);
        }
    }

    // Saturation/food cannot go down in spawn protection
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        BrawlProfile profile = BrawlProfile.get(player);
        if (profile != null && profile.isSpawnProtected()) {
            event.setCancelled(true);
            player.setFoodLevel(20);
            player.setSaturation(20f);
        }
    }
}