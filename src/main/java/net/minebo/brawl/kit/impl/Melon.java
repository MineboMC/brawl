package net.minebo.brawl.kit.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.projectile.BlockProjectile;
import net.minebo.cobalt.projectile.ItemProjectile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ItemBuilder;
import net.minebo.cobalt.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Melon extends Kit {

    private static final String MELON_METADATA = "Melon";

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.MELON_SLICE);
    }

    @Override
    public String getName() {
        return "Melon";
    }

    @Override
    public ChatColor getColor() { return ChatColor.of("#7FCC19"); }

    @Override
    public String getDescription() {
        return "Send enemies flying with Melons!";
    }

    @Override
    public Integer getPrice() { return 100; }

    @Override
    public ItemStack getAbilityItem() {
        return new ItemBuilder(Material.GLISTERING_MELON_SLICE)
                .setName(getColor() + "Melon Toss")
                .build();
    }

    @Override
    public List<ItemStack> getDefaultItems() {
        return List.of(
                new ItemBuilder(Material.MELON_SLICE)
                        .setName(ChatColor.YELLOW + "Melon Smacker")
                        .addEnchantment(Enchantment.SHARPNESS, 8)
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
                        .addEnchantment(Enchantment.UNBREAKING, 10)
                        .setHexColor("#7FCC19")
                        .build(),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemBuilder(Material.LEATHER_LEGGINGS)
                        .addEnchantment(Enchantment.PROTECTION, 1)
                        .addEnchantment(Enchantment.UNBREAKING, 10)
                        .setHexColor("#7FCC19")
                        .build(),
                new ItemBuilder(Material.LEATHER_BOOTS)
                        .addEnchantment(Enchantment.PROTECTION, 1)
                        .addEnchantment(Enchantment.UNBREAKING, 10)
                        .setHexColor("#7FCC19")
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

            Cooldown cd = Brawl.getInstance().getCooldownHandler().getCooldown("Melon Toss");
            if(cd.onCooldown(player)) {
                player.sendMessage(ColorUtil.translateColors("&cYou can't use this for &l" + cd.getRemaining(player)));
                return;
            }

            cd.applyCooldown(player, 10, TimeUnit.SECONDS, Brawl.getInstance());

            new BlockProjectile(Brawl.getInstance(), Material.MELON, 2).withMetadata(MELON_METADATA).shoot(player);
        }
    }

    @EventHandler
    public void onBlockProjectileHit(BlockProjectile.BlockProjectileHitEvent event) {
        FallingBlock projectile = event.getProjectile();
        Player shooter = event.getShooter();
        Entity victimEntity = event.getHitEntity();

        Player victim;

        Player nearestPlayer = LocationUtil.getNearestPlayer(event.getHitLocation(), 3);

        if (victimEntity != null) {
            victim = (Player) victimEntity;
        } else if(nearestPlayer != null) {
            victim = nearestPlayer;
        } else {
            return;
        }

        // Ensure the projectile is a melon and hit an entity/player or found nearest
        if (projectile.getBlockData().getMaterial() == Material.MELON && projectile.hasMetadata(MELON_METADATA)) {

            List<Item> items = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                ItemStack itemStack = new ItemStack(Material.MELON, 1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(MELON_METADATA);
                itemStack.setItemMeta(itemMeta);

                Item item = victim.getWorld().dropItem(victim.getLocation(), itemStack);
                item.setPickupDelay(Integer.MAX_VALUE);

                Vector v = Vector.getRandom();
                v.setX(v.getX() - 0.25f);
                v.setZ(v.getZ() - 0.25f);
                item.setVelocity(v);
                items.add(item);
            }
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> items.forEach(Item::remove), 15L);

            victim.damage(6, shooter); // damage value
            Vector unitVector = projectile.getVelocity().normalize();
            Vector velocity = unitVector.multiply(3);
            velocity.setY(1.6);

            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
                victim.setVelocity(velocity);
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
                victim.setMetadata(MELON_METADATA, new FixedMetadataValue(Brawl.getInstance(), true));
            }, 1L);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (entity.hasMetadata(MELON_METADATA)) {
                event.setDamage(Math.min(20, event.getDamage() / 3.5));
                entity.removeMetadata(MELON_METADATA, Brawl.getInstance());
            }
        }
    }

}
