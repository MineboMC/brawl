package net.minebo.brawl.kit.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.projectile.ItemProjectile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Zeus extends Kit {

    @Override
    public Material getIcon() {
        return Material.BLAZE_ROD;
    }

    @Override
    public String getName() {
        return "Zeus";
    }

    @Override
    public ChatColor getColor() { return ChatColor.of("#FFD700"); }

    @Override
    public String getDescription() {
        return "Shoot lightning bolts at players.";
    }

    @Override
    public Integer getPrice() { return 100; }

    @Override
    public ItemStack getAbilityItem() {
        return new ItemBuilder(Material.BLAZE_ROD)
                .setName(getColor() + "Lightning Bolt")
                .build();
    }

    @Override
    public List<ItemStack> getDefaultItems() {
        return List.of(
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .setUnbreakable(true)
                        .build(),
                getAbilityItem()
        );
    }

    @Override
    public List<ItemStack> getArmor() {
        return List.of(
                new ItemBuilder(Material.LEATHER_HELMET)
                        .addEnchantment(Enchantment.FIRE_PROTECTION, 1)
                        .addEnchantment(Enchantment.UNBREAKING, 20)
                        .setHexColor("#FFD700")
                        .build(),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemBuilder(Material.LEATHER_BOOTS)
                        .addEnchantment(Enchantment.UNBREAKING, 20)
                        .setHexColor("#FFD700")
                        .build()
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

            Cooldown cd = Brawl.getInstance().getCooldownHandler().getCooldown("Bolt");
            if(cd.onCooldown(player)) {
                player.sendMessage(ColorUtil.translateColors("&cYou can't use this for &l" + cd.getRemaining(player)));
                return;
            }

            cd.applyCooldown(player, 30, TimeUnit.SECONDS, Brawl.getInstance());

            new ItemProjectile(Brawl.getInstance(), Material.BLAZE_ROD, 2).shoot(player);

        }
    }

    @EventHandler
    public void onBoltHit(ItemProjectile.CustomItemProjectileHitEvent event) {
        if (event.getProjectile().getItemStack().getType() != Material.BLAZE_ROD) return;

        Location loc = event.getProjectile().getLocation();
        loc.getWorld().strikeLightningEffect(loc); // lightning visuals, no fire

        // Deal damage if we hit a player
        if (event.getHitEntity() instanceof Player hitPlayer) {
            Player shooter = event.getShooter();
            hitPlayer.damage(20.0, shooter);
        }
    }

}