package net.minebo.brawl.shop.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.shop.ShopItem;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Cobwebs extends ShopItem {

    @Override
    public String getName() {
        return "<white>Cobwebs";
    }

    @Override
    public String getDescription() {
        return "Gives you 5 cobwebs.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.COBWEB).setSize(5).build();
    }

    @Override
    public Integer getPrice() {
        return 150;
    }

    @Override
    public void givePurchase(Player player) {
        PlayerInventory inventory = player.getInventory();

        if(inventory.contains(Material.COBWEB)) {
            inventory.addItem(new ItemStack(Material.COBWEB, 5));
        } else {
            inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), new ItemBuilder(Material.COBWEB).setSize(5).build());
        }
    }

}