package net.minebo.brawl.killstreak.impl;

import net.md_5.bungee.api.ChatColor;
import net.minebo.brawl.killstreak.KillStreak;
import net.minebo.brawl.kit.Kit;
import net.minebo.brawl.mongo.model.BrawlProfile;
import net.minebo.cobalt.util.InventoryUtil;
import net.minebo.cobalt.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GoldenApples extends KillStreak {

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
    public Integer getKills() {
        return 3;
    }

    @Override
    public void doReward(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);

        if(profile == null) {
            return;
        }

        PlayerInventory inventory = player.getInventory();

        inventory.setItem(InventoryUtil.getFirstEmptySlot(inventory, Material.MUSHROOM_STEW, Material.BOWL), new ItemBuilder(Material.GOLDEN_APPLE).setSize(3).build());
    }

}
