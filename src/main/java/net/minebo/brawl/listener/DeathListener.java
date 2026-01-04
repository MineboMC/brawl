package net.minebo.brawl.listener;

import lombok.SneakyThrows;
import net.minebo.basalt.api.BasaltAPI;
import net.minebo.basalt.models.profile.GameProfile;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.kit.impl.Chemist;
import net.minebo.brawl.kit.impl.Palioxis;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.scheduler.Scheduler;
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
import org.bukkit.scheduler.BukkitRunnable;

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

        new BukkitRunnable() {

            @Override
            public void run() {
                if(LAST_HITS.containsKey(victim)) {
                    if(LAST_HITS.get(victim).equals(damager)) {
                        LAST_HITS.remove(victim);
                    }
                }
            }

        }.runTaskLater(Brawl.getInstance(), 20 * 20);

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

    @SneakyThrows
    public void processDeath(Player victim, Player killer) {
        BrawlProfile victimProfile = BrawlProfile.get(victim);
        BrawlProfile killerProfile = BrawlProfile.get(killer);

        if(victimProfile == null || killerProfile == null) {
            return;
        }

        if(victimProfile.lastUsername == killerProfile.lastUsername) {
            processDeath(victim);
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

        victim.sendMessage(ColorUtil.translateColors("&cYou died to " + killer.getDisplayName() + ((killerProfile.getSelectedKit() != null ) ? " &cusing " + killerProfile.getSelectedKit().getColoredName() + "&c!" : "&c!")));
        killer.sendMessage(ColorUtil.translateColors("&7You got &2$&a10 &7for killing " + victim.getDisplayName() + "&7!"));

        GameProfile profile = BasaltAPI.INSTANCE.quickFindProfile(killer.getUniqueId()).get();
        Integer extraMoney = getExtraMoney(profile);

        if(extraMoney > 0) {
            killerProfile.money.add(extraMoney);
            killer.sendMessage(ColorUtil.translateColors("&7And another &2$&a" + extraMoney + " &7for being a " + profile.getCurrentRank().getColor() + profile.getCurrentRank().getDisplayName() + "&7!"));
        }

        killer.sendActionBar(ColorUtil.translateColors("&a+ &4$&a" + (10+extraMoney) + " (killed " + victim.getName()) + ")");

        killerProfile.money.add(10);

        killerProfile.save();
        victimProfile.save();

        Kit.handleKillerKit(killer, killerProfile);
        KillStreak.handleKillStreak(killer, killerProfile.killstreak.get());

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

        profile.save();

        victim.sendMessage(ColorUtil.translateColors("&cYou died."));
    }

    public Integer getExtraMoney(GameProfile profile) {

        if(Brawl.getInstance().getConfig().isSet("extra-money." + profile.getCurrentRank().getId().toLowerCase())) {
            return Brawl.getInstance().getConfig().getInt("extra-money." + profile.getCurrentRank().getId().toLowerCase());
        }

        return 0;
    }

}
