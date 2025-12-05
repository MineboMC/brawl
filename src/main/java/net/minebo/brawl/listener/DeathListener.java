package net.minebo.brawl.listener;

import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e){
        if(e.getEntity().getKiller() == null){
            return;
        }

        e.setDeathMessage(null);

        Player killer = e.getEntity().getKiller();
        BrawlProfile killerProfile = BrawlProfile.get(e.getEntity().getKiller());
        BrawlProfile victimProfile = BrawlProfile.get(e.getEntity());

        killerProfile.kills.add(1);
        killerProfile.killstreak.add(1);

        if(killerProfile.killstreak.get() > killerProfile.highestkillstreak.get()) {
            killer.sendMessage(ColorUtil.translateColors("&aNew Record! &fYou've gotten a streak of " + killerProfile.killstreak.get() + "!"));
            killerProfile.highestkillstreak.set(killerProfile.killstreak.get());
        }

        victimProfile.deaths.add(1);
        victimProfile.killstreak.set(0);

        e.getEntity().sendMessage(ColorUtil.translateColors("&cYou died to " + killer.getDisplayName() + " &cusing " + killerProfile.getSelectedKit().getColoredName() + "&c!"));
        killer.sendMessage(ColorUtil.translateColors("&7You got 10 coins for killing " + e.getEntity().getDisplayName() + "&7!"));

        killerProfile.money.add(10);
    }

    @EventHandler
    public void onEnvironmentalDeath(PlayerDeathEvent e){
        if(e.getEntity().getKiller() != null){
            return;
        }

        e.setDeathMessage(null);

        BrawlProfile profile =  BrawlProfile.get(e.getEntity());

        profile.deaths.add(1);
        profile.killstreak.set(0);

        e.getEntity().sendMessage(ColorUtil.translateColors("&cYou died."));
    }

}
