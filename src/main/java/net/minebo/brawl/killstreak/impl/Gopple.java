package net.minebo.brawl.killstreak.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Gopple extends KillStreak {

    @Override
    public String getName() {
        return "God Apple";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    @Override
    public String getDescription() {
        return "Gives you a god apple.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).setSize(getKills()).build();
    }

    @Override
    public Integer getKills() {
        return 35;
    }

    @Override
    public void doReward(Player player) {
        PlayerInventory inventory = player.getInventory();

        inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).setSize(1).build());
    }

}
