package net.minebo.brawl.kit.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.projectile.BlockProjectile;
import net.minebo.cobalt.util.ColorUtil;
import net.minebo.cobalt.util.ItemBuilder;
import net.minebo.cobalt.util.LocationUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Avatar extends Kit {

    private static final String AVATAR_METADATA = "Avatar Flames";
    private final Set<UUID> noFallDamage = new HashSet<>();

    @Override
    public ItemStack getIcon() { return new ItemStack(Material.BEACON); }

    @Override
    public String getName() { return "Avatar"; }

    @Override
    public ChatColor getColor() { return ChatColor.AQUA; }

    @Override
    public String getDescription() { return "Bend the elements!"; }

    @Override
    public Integer getPrice() { return 100; }

    @Override
    public ItemStack getAbilityItem() {
        return new ItemBuilder(Material.LIGHT_BLUE_DYE)
                .setName(getColor() + "Water Gun")
                .build();
    }

    @Override
    public List<ItemStack> getDefaultItems() {
        return List.of(
                new ItemStack(Material.DIAMOND_SWORD),
                getAbilityItem()
        );
    }

    @Override
    public List<ItemStack> getArmor() {
        return List.of(
                new ItemStack(Material.CHAINMAIL_HELMET),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.CHAINMAIL_LEGGINGS),
                new ItemStack(Material.IRON_BOOTS)
        );
    }

    @Override
    public List<PotionEffect> getEffects() {
        return List.of(new PotionEffect(PotionEffectType.SPEED, -1, 1));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        Player player = event.getPlayer();
        if (!hasKitOn(player)) return;

        // Water Gun
        if (event.getItem().isSimilar(getAbilityItem()) &&
                (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {

            event.setCancelled(true);
            BrawlProfile profile = BrawlProfile.get(player);

            if (profile.isSpawnProtected()) {
                player.sendMessage(ColorUtil.translateColors("&cYou can't use this ability while protected by spawn."));
                return;
            }

            Cooldown cd = Brawl.getInstance().getCooldownHandler().getCooldown("Water Gun");
            if (cd.onCooldown(player)) {
                player.sendMessage(ColorUtil.translateColors("&cYou can't use this for &l" + cd.getRemaining(player)));
                return;
            }
            cd.applyCooldown(player, 15, TimeUnit.SECONDS, Brawl.getInstance());
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
            new BlockProjectile(Brawl.getInstance(), Material.ICE, 4)
                    .withMetadata("WATER_GUN")
                    .shoot(player);
        }
    }

    @EventHandler
    public void onBlockProjectileHit(BlockProjectile.BlockProjectileHitEvent event) {
        FallingBlock projectile = event.getProjectile();
        Player shooter = event.getShooter();

        if (projectile.hasMetadata("WATER_GUN")) {
            Entity victimEntity = event.getHitEntity();
            Location hitLoc = event.getHitEntity() == null ? event.getHitLocation() : (victimEntity != null ? victimEntity.getLocation() : projectile.getLocation());
            createTemporaryWater(hitLoc);

            if (victimEntity instanceof Player) {
                Player victim = (Player) victimEntity;
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
                victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);

                // Play sound for nearby players
                for (Entity entity : victim.getNearbyEntities(5, 5, 5)) {
                    if (entity instanceof Player) {
                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 1);
                    }
                }
            }
        }
    }

    private void createTemporaryWater(Location loc) {
        List<Location> waterBlocks = new ArrayList<>();
        List<Location> lavaBlocksToRestore = new ArrayList<>();
        List<Material> originalMaterials = new ArrayList<>();

        World world = loc.getWorld();
        for (int x = -1; x <= 1; x++)
            for (int z = -1; z <= 1; z++) {
                Location waterLoc = loc.clone().add(x, 0, z);
                Material originalType = waterLoc.getBlock().getType();

                if (waterLoc.getBlock().getType() == Material.AIR) {
                    waterLoc. getBlock().setType(Material.WATER);
                    org.bukkit.block.data.BlockData data = waterLoc.getBlock().getBlockData();
                    if (data instanceof org.bukkit.block.data. Levelled levelled) {
                        levelled.setLevel(0); // source
                        waterLoc.getBlock().setBlockData(levelled, false);
                    }
                    waterLoc.getBlock().setMetadata("avatar_water", new FixedMetadataValue(Brawl.getInstance(), true));
                    waterBlocks.add(waterLoc);
                }

                // Store lava blocks that might turn into obsidian
                for (int lx = -1; lx <= 1; lx++) {
                    for (int ly = -1; ly <= 1; ly++) {
                        for (int lz = -1; lz <= 1; lz++) {
                            Location lavaCheck = waterLoc.clone().add(lx, ly, lz);
                            if (lavaCheck.getBlock().getType() == Material.LAVA) {
                                lavaBlocksToRestore.add(lavaCheck. clone());
                                originalMaterials.add(Material.LAVA);
                            }
                        }
                    }
                }
            }

        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            for (Location l : waterBlocks) {
                if (l.getBlock().getType() == Material.WATER) {
                    l.getBlock().removeMetadata("avatar_water", Brawl.getInstance());
                    l.getBlock().setType(Material.AIR);
                }
            }

            // Restore any lava that was converted
            for (int i = 0; i < lavaBlocksToRestore.size(); i++) {
                Location lavaLoc = lavaBlocksToRestore.get(i);
                if (lavaLoc.getBlock().getType() == Material.OBSIDIAN ||
                        lavaLoc. getBlock().getType() == Material.COBBLESTONE ||
                        lavaLoc.getBlock().getType() == Material.STONE) {
                    lavaLoc.getBlock().setType(originalMaterials.get(i));
                }
            }
        }, 40L);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        // Check if the block forming is next to avatar water
        Location formLoc = event.getBlock().getLocation();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location checkLoc = formLoc.clone().add(x, y, z);
                    if (checkLoc.getBlock().hasMetadata("avatar_water")) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    // Add these event handlers to your Kit class
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().hasMetadata("avatar_water")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getSource().hasMetadata("avatar_water")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if(event.getPlayer().isOnGround()) return;
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (!hasKitOn(player)) return;

        performAvatarJump(player);
    }

    private void performAvatarJump(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        if (profile.isSpawnProtected()) {
            player.sendMessage(ColorUtil.translateColors("&cYou can't use this ability while protected by spawn."));
            return;
        }

        Cooldown cd = Brawl.getInstance().getCooldownHandler().getCooldown("Avatar Jump");
        if (cd.onCooldown(player)) {
            player.sendMessage(ColorUtil.translateColors("&cYou can't use this for &l" + cd.getRemaining(player)));
            return;
        }
        cd.applyCooldown(player, 15, TimeUnit.SECONDS, Brawl.getInstance());

        player.setVelocity(player.getLocation().getDirection().normalize().multiply(2));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1);

        player.setMetadata(AVATAR_METADATA, new FixedMetadataValue(Brawl.getInstance(), true));
        if (player.getLocation().getPitch() <= -15) {
            noFallDamage.add(player.getUniqueId());
        }
        Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
            player.removeMetadata(AVATAR_METADATA, Brawl.getInstance());
        }, 20L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata(AVATAR_METADATA) || BrawlProfile.get(player).isSpawnProtected()) return;

        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 0.25, 0), 8, 0.3, 0.5, 0.3, 0.02);

        // Light nearby players
        for (Entity entity : player.getNearbyEntities(2, 2, 2)) {
            if (entity instanceof Player victim && !victim.equals(player)) {
                victim.setFireTicks(40);
                victim.playSound(victim.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1, 1);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        // Avatar jump custom fall damage
        if (entity instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (hasKitOn(player)) {
                event.setCancelled(true);
            }
        }
    }

}
