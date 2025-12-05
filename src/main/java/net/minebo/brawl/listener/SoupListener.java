package net.minebo.brawl.listener;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.cooldown.construct.Cooldown;
import net.minebo.cobalt.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class SoupListener implements Listener {

    @EventHandler
    public void onUseSoup(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Check if player is holding mushroom soup
        if (item != null && item.getType() == Material.MUSHROOM_STEW) {
            double maxHealth = player.getMaxHealth();
            double healAmount = 7.0;

            int maxHunger = 20;
            int foodAmount = 7;

            boolean consumed = false;

            // Restore hunger
            if (player.getFoodLevel() < maxHunger) {
                int newFoodLevel = Math.min(player.getFoodLevel() + foodAmount, maxHunger);
                player.setFoodLevel(newFoodLevel);

                // Set fixed saturation of 3.5, capped at the current food level
                float newSaturation = Math.min(player.getSaturation() + 7, newFoodLevel);
                player.setSaturation(newSaturation);
                consumed = true;
            }

            // Heal if necessary
            if (player.getHealth() < maxHealth) {
                double newHealth = Math.min(player.getHealth() + healAmount, maxHealth);
                player.setHealth(newHealth);
                consumed = true;
            }

            // Replace soup with bowl
            if (consumed) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if(event.getItemDrop().getItemStack().getType() != Material.BOWL && event.getItemDrop().getItemStack().getType() != Material.MUSHROOM_STEW) {
            event.setCancelled(true);
            player.sendMessage(ColorUtil.translateColors("&cYou can't drop that."));
            return;
        }

        if(event.getItemDrop().getItemStack().getType() == Material.BOWL || event.getItemDrop().getItemStack().getType() == Material.MUSHROOM_STEW) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Soup]")) {
            event.setLine(0, "");
            event.setLine(1, ColorUtil.translateColors("&9- &lFree &9-"));
            event.setLine(2, ColorUtil.translateColors("&9Soup"));
        }
    }

    @EventHandler
    public void onSignEdit(PlayerOpenSignEvent event) {
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }

        Sign sign = event.getSign();

        // Compare text on sign (assuming color format)
        if (sign.getLine(1).equalsIgnoreCase(ColorUtil.translateColors("&9- &lFree &9-")) &&
                sign.getLine(2).equalsIgnoreCase(ColorUtil.translateColors("&9Soup"))) {

            Player player = event.getPlayer();
            BrawlProfile profile = BrawlProfile.get(player);

            Cooldown soupCooldown = Brawl.getInstance().getCooldownHandler().getCooldown("Free Soup");

            if (soupCooldown.onCooldown(player)) {
                player.sendMessage(ColorUtil.translateColors("&cYou can't use this for &l" + soupCooldown.getRemaining(player)));
                return;
            }

            Inventory inventory = Bukkit.createInventory(null, 54, "Free Soup");
            Kit.fillInvWithSoup(inventory);
            player.openInventory(inventory);
            soupCooldown.applyCooldown(player, 60, TimeUnit.SECONDS, Brawl.getInstance());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Remove all drops (we'll add soup bowls manually)
        event.getDrops().clear();

        // Drop all mushroom soup from inventory and despawn after 5 seconds
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.MUSHROOM_STEW) {
                Location deathLoc = player.getLocation();
                Item droppedItem = deathLoc.getWorld().dropItemNaturally(deathLoc, item.clone());

                // Schedule removal after 5 seconds (100 ticks)
                Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
                    if (!droppedItem.isDead()) {
                        droppedItem.remove();
                    }
                }, 100L);
            }
        }
    }
}

