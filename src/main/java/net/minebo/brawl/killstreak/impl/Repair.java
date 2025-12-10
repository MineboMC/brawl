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

import java.util.List;

public class Repair extends KillStreak {

    @Override
    public String getName() {
        return "Armor Repair";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.RED;
    }

    @Override
    public String getDescription() {
        return "Replaces your armor with a fresh set.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.ANVIL).setSize(getKills()).build();
    }

    @Override
    public Integer getKills() {
        return 25;
    }

    @Override
    public void doReward(Player player) {
        BrawlProfile profile = BrawlProfile.get(player);
        if(profile == null) return;

        PlayerInventory inventory = player.getInventory();

        Kit kit = profile.getSelectedKit();
        List<ItemStack> armor = kit.getArmor();

        inventory.setHelmet(armor.get(0));
        inventory.setChestplate(armor.get(1));
        inventory.setLeggings(armor.get(2));
        inventory.setBoots(armor.get(3));
    }

}
