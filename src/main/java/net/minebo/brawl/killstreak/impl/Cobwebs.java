package net.minebo.brawl.killstreak.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Cobwebs extends KillStreak {

    @Override
    public String getName() {
        return "Cobwebs";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.WHITE;
    }

    @Override
    public String getDescription() {
        return "Gives you 5 cobwebs.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.COBWEB).setSize(getKills()).build();
    }

    @Override
    public Integer getKills() {
        return 15;
    }

    @Override
    public void doReward(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), new ItemBuilder(Material.COBWEB).setSize(5).build());
    }

}
