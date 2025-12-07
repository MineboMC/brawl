package net.minebo.brawl.listener;

import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;

public class DeathListener implements Listener {

    public HashMap<Player, Player> LAST_HITS = new HashMap<>();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) e.getDamager();
        Player victim = (Player) e.getEntity();

        LAST_HITS.put(victim, damager);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e){
        if(e.getEntity().getKiller() == null){
            return;
        }

        e.setDeathMessage(null);

        Player killer = e.getEntity().getKiller();

        processDeath(e.getEntity(), killer);
    }

    @EventHandler
    public void onEnvironmentalDeath(PlayerDeathEvent e){
        if(e.getEntity().getKiller() != null){
            return;
        }

        e.setDeathMessage(null);

        if(LAST_HITS.containsKey(e.getEntity())){
            processDeath(e.getEntity(), LAST_HITS.get(e.getEntity()));
            return;
        }

        processDeath(e.getEntity());
    }

    public void processDeath(Player victim, Player killer) {
        BrawlProfile victimProfile = BrawlProfile.get(victim);
        BrawlProfile killerProfile = BrawlProfile.get(killer);

        if(victimProfile == null || killerProfile == null) {
            return;
        }

        killerProfile.kills.add(1);
        killerProfile.killstreak.add(1);

        if(killerProfile.killstreak.get() > killerProfile.highestkillstreak.get()) {
            killer.sendMessage(ColorUtil.translateColors("&aNew Record! &fYou've gotten a streak of " + killerProfile.killstreak.get() + "!"));
            killerProfile.highestkillstreak.set(killerProfile.killstreak.get());
        }

        victimProfile.deaths.add(1);
        victimProfile.killstreak.set(0);

        victim.sendMessage(ColorUtil.translateColors("&cYou died to " + killer.getDisplayName() + " &cusing " + killerProfile.getSelectedKit().getColoredName() + "&c!"));
        killer.sendMessage(ColorUtil.translateColors("&7You got 10 coins for killing " + victim.getDisplayName() + "&7!"));

        killerProfile.money.add(10);

        if(LAST_HITS.containsKey(victim)){
            LAST_HITS.remove(victim);
        }
    }

    public void processDeath(Player victim) {
        if(BrawlProfile.get(victim) == null) {
            return;
        }

        BrawlProfile profile = BrawlProfile.get(victim);

        profile.deaths.add(1);
        profile.killstreak.set(0);

        victim.sendMessage(ColorUtil.translateColors("&cYou died."));
    }

}
