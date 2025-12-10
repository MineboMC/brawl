package net.minebo.brawl.killstreak.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AttackDogs extends KillStreak {

    private static final Map<UUID, List<UUID>> OWNER_WOLVES = new ConcurrentHashMap<>();

    private static final int DURATION_SECONDS = 30;
    private static final long TASK_PERIOD_TICKS = 10L;
    private static final double TARGET_RADIUS = 8.0;
    private static final double TELEPORT_DISTANCE = 12.0;

    @Override
    public String getName() {
        return "Attack Dogs";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }

    @Override
    public String getDescription() {
        return "Spawns attack dogs around you.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.WOLF_SPAWN_EGG).setSize(getKills()).build();
    }

    @Override
    public Integer getKills() {
        return 42;
    }

    public static void registerWolves(UUID owner, List<Wolf> wolves) {
        OWNER_WOLVES.put(owner, wolves.stream().map(Entity::getUniqueId).collect(Collectors.toList()));
    }

    public static void removeWolvesFor(UUID owner) {
        List<UUID> ids = OWNER_WOLVES.remove(owner);
        if (ids == null || ids.isEmpty()) return;

        for (UUID id : ids) {
            Entity e = Bukkit.getEntity(id);
            if (e instanceof Wolf && e.isValid()) {
                e.remove();
            }
        }
    }

    @Override
    public void doReward(Player player) {
        List<Wolf> wolves = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class);

            wolf.setOwner(player);
            wolf.setTamed(true);
            wolf.setAgeLock(true);
            wolf.setAdult();

            try {
                // keep them beefy
                wolf.setMaxHealth(100);
                wolf.setHealth(wolf.getMaxHealth());
            } catch (NoSuchMethodError ignored) {
                // API differences fallback
                try {
                    wolf.setHealth(20.0);
                } catch (Exception ignored2) {
                }
            }

            wolf.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 1));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));

            wolves.add(wolf);
        }

        // register the wolves so they can be removed later if the owner dies
        registerWolves(player.getUniqueId(), wolves);

        // obtain plugin instance for scheduling
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(AttackDogs.class);

        // schedule a repeating task that makes wolves act as bodyguards for DURATION_SECONDS
        new BukkitRunnable() {
            private int runs = 0;
            private final int maxRuns = (int) Math.ceil((DURATION_SECONDS * 20.0) / TASK_PERIOD_TICKS);

            @Override
            public void run() {
                runs++;
                if (!player.isOnline() || runs > maxRuns) {
                    // cleanup: remove wolves spawned by this reward when time is up or player disconnected
                    removeWolvesFor(player.getUniqueId());
                    cancel();
                    return;
                }

                for (Wolf w : new ArrayList<>(wolves)) {
                    if (w == null || !w.isValid() || w.isDead()) {
                        // if the wolf is gone, don't try to update it further
                        continue;
                    }

                    // keep the wolf near the player
                    if (w.getLocation().distance(player.getLocation()) > TELEPORT_DISTANCE) {
                        w.teleport(player.getLocation().add(Math.random() - 0.5, 0, Math.random() - 0.5));
                    }

                    // find a target near the owner (players or hostile mobs)
                    LivingEntity target = null;
                    for (Entity e : player.getNearbyEntities(TARGET_RADIUS, TARGET_RADIUS, TARGET_RADIUS)) {
                        if (!(e instanceof LivingEntity)) continue;
                        if (e.equals(player)) continue;
                        if (e instanceof Wolf) continue; // don't attack other wolves
                        // set first valid living entity as target
                        target = (LivingEntity) e;
                        break;
                    }

                    if (target != null) {
                        // set the wolf's target so it defends the player
                        try {
                            w.setTarget(target);
                        } catch (Exception ignored) {
                        }
                    } else {
                        // optional: clear target or make it follow the player
                        // w.setTarget(null);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, TASK_PERIOD_TICKS);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        AttackDogs.removeWolvesFor(dead.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AttackDogs.removeWolvesFor(player.getUniqueId());
    }

}