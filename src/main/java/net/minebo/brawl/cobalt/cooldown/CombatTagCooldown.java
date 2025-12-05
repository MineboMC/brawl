package net.minebo.brawl.cobalt.cooldown;

import net.minebo.brawl.Brawl;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.TimeUnit;

public class CombatTagCooldown extends Cooldown {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;

        Player attacker = null;

        // Arrow (projectile) case
        if (e.getDamager() instanceof Arrow arrow) {
            if (arrow.getShooter() instanceof Player shooter) {
                attacker = shooter;
            }
        }
        // Melee case
        else if (e.getDamager() instanceof Player playerDamager) {
            attacker = playerDamager;
        }

        // Not player-vs-player
        if (attacker == null || !(e.getEntity() instanceof Player victim)) return;

        BrawlProfile victimProfile = BrawlProfile.get(victim);
        BrawlProfile attackerProfile = BrawlProfile.get(attacker);

        if (victimProfile.isSpawnProtected() || attackerProfile.isSpawnProtected()) return;

        applyCooldown(victim, 30, TimeUnit.SECONDS, Brawl.getInstance());
        applyCooldown(attacker, 30, TimeUnit.SECONDS, Brawl.getInstance());
    }

}
