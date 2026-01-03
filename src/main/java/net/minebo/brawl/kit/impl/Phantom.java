package net.minebo.brawl.kit.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.timer.Timer;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Phantom extends Kit {

    public static FlightTimer flightTimer = new FlightTimer(Brawl.getInstance());

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.PHANTOM_MEMBRANE);
    }

    @Override
    public String getName() {
        return "Phantom";
    }

    @Override
    public ChatColor getColor() { return ChatColor.of("#6E516B"); }

    @Override
    public String getDescription() {
        return "Soar through the sky like a Phantom!";
    }

    @Override
    public Integer getPrice() { return 100; }

    @Override
    public ItemStack getAbilityItem() {
        return new ItemBuilder(Material.FEATHER)
                .setName(getColor() + "Phantom Flight")
                .build();
    }

    @Override
    public List<ItemStack> getDefaultItems() {
        return List.of(
                new ItemBuilder(Material.IRON_SWORD)
                        .addEnchantment(Enchantment.SHARPNESS, 1)
                        .setUnbreakable(true)
                        .build(),
                getAbilityItem()
        );
    }

    @Override
    public List<ItemStack> getArmor() {
        return List.of(
                new ItemBuilder(Material.LEATHER_HELMET)
                        .addEnchantment(Enchantment.PROTECTION, 1)
                        .addEnchantment(Enchantment.UNBREAKING, 20)
                        .setHexColor("#6E516B")
                        .build(),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_BOOTS)
        );
    }

    @Override
    public List<PotionEffect> getEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, -1, 1));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getItem() == null) return;

        Player player = event.getPlayer();

        if(hasKitOn(player) && event.getItem().isSimilar(getAbilityItem()) && (event.getAction() == Action.RIGHT_CLICK_BLOCK ||  event.getAction() == Action.RIGHT_CLICK_AIR)) {

            BrawlProfile profile = BrawlProfile.get(player);
            if(profile.isSpawnProtected()) {
                player.sendMessage(ColorUtil.translateColors("&cYou can't use this ability while protected by spawn."));
                return;
            }

            Cooldown cd = Brawl.getInstance().getCooldownHandler().getCooldown("Flight");
            if(cd.onCooldown(player)) {
                player.sendMessage(ColorUtil.translateColors("&cYou can't use this for &l" + cd.getRemaining(player)));
                return;
            }

            cd.applyCooldown(player, 15, TimeUnit.SECONDS, Brawl.getInstance());

            flightTimer.start(player);

        }
    }

    @EventHandler
    public void onTakeDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        BrawlProfile profile = BrawlProfile.get(player);

        if (flightTimer.hasTimer(player.getUniqueId())) {
            player.setAllowFlight(false);
            player.setFlying(false);

            player.sendMessage(ColorUtil.translateColors("&7Your flight has been disabled due to being hit."));

            flightTimer.cancel(player);
        }
    }

    public static class FlightTimer extends Timer {

        public FlightTimer(Plugin plugin) {
            super(5, plugin);
        }

        @Override
        protected void onStart(Player player) {
            player.setAllowFlight(true);
            player.setFlying(true);

            player.sendMessage(ColorUtil.translateColors("&eYou can now fly for 5 seconds!"));
        }

        @Override
        protected void onComplete(Player player) {
            player.sendMessage(ColorUtil.translateColors("&cYou can no longer fly."));
            player.setFlying(false);
            player.setAllowFlight(false);
        }

    }

}