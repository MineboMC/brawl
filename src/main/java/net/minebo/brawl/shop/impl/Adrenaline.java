package net.minebo.brawl.shop.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.shop.ShopItem;
import net.minebo.cobalt.util.BottleType;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import net.minebo.cobalt.util.PotionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Adrenaline extends ShopItem {

    ItemStack potion = new PotionBuilder(BottleType.DRINK).setName("<red>Adrenaline").setColor(Color.RED).addEffect(PotionEffectType.SPEED, 3, 5).addEffect(PotionEffectType.STRENGTH, 2, 5).build();

    @Override
    public String getName() {
        return "<red>Adrenaline";
    }

    @Override
    public String getDescription() {
        return "Gives you Strength 2 and Speed 3 for 5 seconds.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.DRAGON_BREATH).build();
    }

    @Override
    public Integer getPrice() {
        return 200;
    }

    @Override
    public void givePurchase(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), potion);
    }

    @EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack item = event.getItem();

        if(item.isSimilar(potion)) {
            Bukkit.getScheduler().runTaskLater(Brawl.getInstance(), () -> {
                inventory.remove(Material.GLASS_BOTTLE);
            }, 1L);
        }
    }

}
