package net.minebo.brawl.listener;

import net.minebo.brawl.kit.impl.Chemist;
import net.minebo.brawl.kit.impl.Palioxis;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.PotionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.HashMap;

public class DeathListener implements Listener {

    public HashMap<Player, Player> LAST_HITS = new HashMap<>();

    @EventHandler
    public void onPlayerFallVoid(PlayerMoveEvent e) {
        if(e.getTo().getY() <= 60) {
            e.getPlayer().damage(9000); // arbitrary big number
        }
    }

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

        handleKillerKit(killer, killerProfile);

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

    public void handleKillerKit(Player player, BrawlProfile profile) {
        Inventory inv =  player.getInventory();

        if(profile.getSelectedKit() instanceof Chemist) {
            inv.setItem(1, new PotionBuilder(Material.SPLASH_POTION)
                    .setBasePotionType(PotionType.STRONG_HARMING)
                    .setSize(3)
                    .build());

            inv.setItem(2, new PotionBuilder(Material.SPLASH_POTION)
                    .setBasePotionType(PotionType.STRONG_POISON)
                    .build());

            player.sendMessage(ColorUtil.translateColors("&7Your potions have been replenished."));
        }

        if(profile.getSelectedKit() instanceof Palioxis) {
            inv.setItem(1, new ItemStack(Material.ENDER_PEARL));

            player.sendMessage(ColorUtil.translateColors("&7Your pearl has been replenished."));
        }
    }

}
