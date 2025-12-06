package net.minebo.brawl.kit.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ItemBuilder;
import net.minebo.cobalt.util.LocationUtil;
import net.minebo.cobalt.util.ParticleEffect;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Stomper extends Kit implements Listener {

    private static final String STOMPER_METADATA = "StomperCharge";

    @Override
    public Material getIcon() { return Material.ANVIL; }

    @Override
    public String getName() { return "Stomper"; }

    @Override
    public ChatColor getColor() { return ChatColor.of("#FF0000"); }

    @Override
    public String getDescription() { return "Stomp on your enemies to kill them!"; }

    @Override
    public Integer getPrice() { return 100; }

    @Override
    public ItemStack getAbilityItem() {
        return new ItemBuilder(Material.ANVIL)
                .setName(getColor() + "Stomp")
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
                        .setHexColor("#FF0000")
                        .build(),
                new ItemBuilder(Material.GOLDEN_CHESTPLATE)
                        .addEnchantment(Enchantment.PROTECTION, 2)
                        .build(),
                new ItemBuilder(Material.GOLDEN_LEGGINGS)
                        .addEnchantment(Enchantment.PROTECTION, 2)
                        .build(),
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

        if(!hasKitOn(player) || !event.getItem().isSimilar(getAbilityItem())) return;
        if(!(event.getAction().toString().contains("RIGHT_CLICK"))) return;

        BrawlProfile profile = BrawlProfile.get(player);
        if(profile.isSpawnProtected()) {
            player.sendMessage(ColorUtil.translateColors("&cYou can't use this ability while protected by spawn."));
            return;
        }

        Cooldown cd = Brawl.getInstance().getCooldownHandler().getCooldown("Stomp");
        if(cd.onCooldown(player)) {
            player.sendMessage(ColorUtil.translateColors("&cYou can't use this for &l" + cd.getRemaining(player)));
            return;
        }

        // Only launch if on ground
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must be on the ground to launch!");
            return;
        }
        // Launch up
        cd.applyCooldown(player, 15, TimeUnit.SECONDS, Brawl.getInstance());
        Vector velocity = new Vector(0, 3.0, 0);
        player.setVelocity(velocity);
        player.setMetadata(STOMPER_METADATA, new FixedMetadataValue(Brawl.getInstance(), true));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 2f);
        ParticleEffect.CLOUD.display(player.getLocation(), 10, .5, 0, .5, .2f, player);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!hasKitOn(player) || !player.hasMetadata(STOMPER_METADATA)) return;
        if (player.isOnGround()) return;

        player.setVelocity(new Vector(0, -3.5, 0));
        ParticleEffect.EXPLOSION.display(player.getLocation(), 5, 0, 0, 0, .4f, player);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1f, 0f);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasKitOn(player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && player.hasMetadata(STOMPER_METADATA)) {
            event.setDamage(0);

            List<Player> nearbyPlayers = LocationUtil.getNearbyPlayers(player, 5);
            for (Player target : nearbyPlayers) {
                if (target.equals(player)) continue; // <-- SKIP the stomper!
                double damage = Math.min(32.0, player.getFallDistance() / 2.0);
                target.damage(damage, player);
                target.sendMessage(ColorUtil.translateColors("&eYou were stomped by " + player.getDisplayName() + "&e!"));
            }

            // Show effect to all nearby/online players
            ParticleEffect.EXPLOSION.display(player.getLocation(), 8, 0, 0, 0, .3f, Bukkit.getOnlinePlayers().toArray(new Player[0]));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);

            player.removeMetadata(STOMPER_METADATA, Brawl.getInstance());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (hasKitOn(player) && player.hasMetadata(STOMPER_METADATA)) {
            player.removeMetadata(STOMPER_METADATA, Brawl.getInstance());
        }
    }
}