package net.minebo.brawl.shop.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.shop.ShopItem;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Gapples extends ShopItem {

    @Override
    public String getName() {
        return "Golden Apples";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public String getDescription() {
        return "Gives you 3 Golden Apples.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.GOLDEN_APPLE).setSize(3).build();
    }

    @Override
    public Integer getPrice() {
        return 100;
    }

    @Override
    public void givePurchase(Player player) {
        PlayerInventory inventory = player.getInventory();

        if(inventory.contains(Material.GOLDEN_APPLE)) {
            inventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 3));
        } else {
            inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), new ItemBuilder(Material.GOLDEN_APPLE).setSize(3).build());
        }
    }

}
