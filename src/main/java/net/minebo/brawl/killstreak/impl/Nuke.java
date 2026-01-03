package net.minebo.brawl.killstreak.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.Brawl;
import net.minebo.brawl.cobalt.timer.NukeTimer;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Nuke extends KillStreak {

    @Override
    public String getName() {
        return "Nuke";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public String getDescription() {
        return "Spawns a nuke.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.TNT).setSize(getKills()).build();
    }

    @Override
    public Integer getKills() {
        return 50;
    }

    @Override
    public void doReward(Player player) {
        Brawl.getInstance().getNukeTimer().start(player);
    }

}
